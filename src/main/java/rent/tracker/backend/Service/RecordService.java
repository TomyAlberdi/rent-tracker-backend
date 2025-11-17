package rent.tracker.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rent.tracker.backend.DTO.Record.CreateRecordDTO;
import rent.tracker.backend.DTO.Record.MonthlySummaryRecordDTO;
import rent.tracker.backend.DTO.Record.YearlyParentSummaryRecordDTO;
import rent.tracker.backend.Exception.ResourceNotFoundException;
import rent.tracker.backend.Mapper.RecordMapper;
import rent.tracker.backend.Model.Group;
import rent.tracker.backend.Model.Property;
import rent.tracker.backend.Model.Record;
import rent.tracker.backend.Model.Transaction;
import rent.tracker.backend.Repository.GroupRepository;
import rent.tracker.backend.Repository.PropertyRepository;
import rent.tracker.backend.Repository.RecordRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class RecordService {
    
    private final RecordRepository recordRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final GroupRepository groupRepository;
    
    public List<Record> getByParentIdAndYear(Property.PropertyType type, String parentId, Integer year) {
        checkParentExists(type, parentId);
        return recordRepository.findAllByParentIdAndYear(parentId, year);
    }
    
    @Transactional
    public Record save(CreateRecordDTO recordDTO) {
        String parentName = checkParentExists(recordDTO.getType(), recordDTO.getParentId());
        Record savedRecord;
        Record record = recordRepository.findById(recordDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Record with id " + recordDTO.getId() + " not found"));
        if (recordDTO.getId() == null) {
            Record newRecord = RecordMapper.toEntity(recordDTO, parentName);
            calculateAmounts(newRecord);
            savedRecord = recordRepository.save(newRecord);
        } else {
            RecordMapper.updateFromDTO(record, recordDTO, parentName);
            calculateAmounts(record);
            savedRecord = recordRepository.save(record);
        }
        syncPropertyRecordToGroupRecord(savedRecord);
        if (recordDTO.getTransactions().isEmpty()) {
            recordRepository.delete(record);
            return null;
        }
        return savedRecord;
    }
    
    @Transactional
    public void delete(String id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record with id " + id + " not found"));
        removePropertyRecordFromGroupRecord(record);
        recordRepository.delete(record);
    }
    
    private Transaction createSummaryTransaction(Transaction.TransactionType type, String propertyId, String propertyName, BigDecimal amount) {
        Transaction t = new Transaction();
        String prefix = (type == Transaction.TransactionType.INCOME) ? "INGRESOS" : "GASTOS";
        t.setTitle(prefix + " " + propertyName);
        t.setAmount(amount == null ? BigDecimal.ZERO : amount);
        t.setType(type);
        t.setMetaPropertyId(propertyId);
        return t;
    }
    
    @Transactional
    protected void syncPropertyRecordToGroupRecord(Record propertyRecord) {
        if (propertyRecord.getType() != Property.PropertyType.INDIVIDUAL) {
            return;
        }
        Property property = propertyService.getPropertyById(propertyRecord.getParentId());
        if (property.getGroupId() == null) {
            return; // Property is not part of a group, nothing to sync
        }
        Record groupRecord = recordRepository
                .findByTypeAndParentIdAndMonthAndYear(Property.PropertyType.GROUPED, property.getGroupId(), propertyRecord.getMonth(), propertyRecord.getYear())
                .orElseGet(() -> {
                    Record newGroupRecord = new Record();
                    newGroupRecord.setType(Property.PropertyType.GROUPED);
                    newGroupRecord.setParentId(property.getGroupId());
                    newGroupRecord.setYear(propertyRecord.getYear());
                    newGroupRecord.setMonth(propertyRecord.getMonth());
                    newGroupRecord.setTransactions(new ArrayList<>());
                    return newGroupRecord;
                });
        groupRecord.getTransactions()
                .removeIf(t -> property.getId().equals(t.getMetaPropertyId()));
        BigDecimal income = sumAmounts(Transaction.TransactionType.INCOME, propertyRecord.getTransactions());
        BigDecimal expenses = sumAmounts(Transaction.TransactionType.EXPENSE, propertyRecord.getTransactions());
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            Transaction incomeSummary = createSummaryTransaction(Transaction.TransactionType.INCOME, property.getId(), property.getName(), income);
            groupRecord.getTransactions().add(incomeSummary);
        }
        if (expenses.compareTo(BigDecimal.ZERO) > 0) {
            Transaction expenseSummary = createSummaryTransaction(Transaction.TransactionType.EXPENSE, property.getId(), property.getName(), expenses);
            groupRecord.getTransactions().add(expenseSummary);
        }
        calculateAmounts(groupRecord);
        this.save(RecordMapper.toDTO(groupRecord));
    }
    
    @Transactional
    protected void removePropertyRecordFromGroupRecord(Record propertyRecord) {
        if (propertyRecord.getType() != Property.PropertyType.INDIVIDUAL) {
            return;
        }
        Property property = propertyService.getPropertyById(propertyRecord.getParentId());
        if (property.getGroupId() == null) {
            return;
        }
        
        Optional<Record> optionalGroupRecord = recordRepository
                .findByTypeAndParentIdAndMonthAndYear(Property.PropertyType.GROUPED, property.getGroupId(), propertyRecord.getMonth(), propertyRecord.getYear());
        if (optionalGroupRecord.isEmpty()) {
            return;
        }
        Record groupRecord = optionalGroupRecord.get();
        boolean removed = groupRecord.getTransactions()
                .removeIf(t -> property.getId().equals(t.getMetaPropertyId()));
        if (removed) {
            calculateAmounts(groupRecord);
            this.save(RecordMapper.toDTO(groupRecord));
        }
    }
    
    public List<MonthlySummaryRecordDTO> getMonthlySummary(Integer year) {
        List<Record> yearlyRecords = recordRepository.findAllByYearOrderByMonth(year);
        Map<Integer, MonthlySummaryRecordDTO> summaryMap = getEmptyMonthlySummary(year);
        for (Record record: yearlyRecords) {
            MonthlySummaryRecordDTO summary = summaryMap.get(record.getMonth());
            if (summary != null) {
                summary.setTotalIncome(summary.getTotalIncome().add(record.getTotalIncome() != null ? record.getTotalIncome() : BigDecimal.ZERO));
                summary.setTotalExpense(summary.getTotalExpense().add(record.getTotalExpense() != null ? record.getTotalExpense() : BigDecimal.ZERO));
                summary.setNetIncome(summary.getNetIncome().add(record.getNetIncome() != null ? record.getNetIncome() : BigDecimal.ZERO));
            }
        }
        return IntStream.rangeClosed(1,12).mapToObj(summaryMap::get).toList();
    }
    
    public List<YearlyParentSummaryRecordDTO> getParentSummaryRecord(Integer year, Property.PropertyType type) {
        List<Record> yearlyRecords = recordRepository.findAllByYearAndTypeOrderByParentName(year, type);
        Map<String, YearlyParentSummaryRecordDTO> grouped = new HashMap<>();
        for (Record record : yearlyRecords) {
            String parentId = record.getParentId();
            YearlyParentSummaryRecordDTO dto = grouped.get(parentId);
            if (dto == null) {
                dto = new YearlyParentSummaryRecordDTO();
                dto.setYear(year);
                dto.setParentType(type);
                dto.setParentId(parentId);
                dto.setParentName(record.getParentName());
                dto.setNetIncome(BigDecimal.ZERO);
                grouped.put(parentId, dto);
            }
            dto.setNetIncome(dto.getNetIncome().add(record.getNetIncome() != null ? record.getNetIncome() : BigDecimal.ZERO));
        }
        List<YearlyParentSummaryRecordDTO> sorted = new ArrayList<>(grouped.values());
        sorted.sort(Comparator.comparing(YearlyParentSummaryRecordDTO::getNetIncome).reversed());
        
        List<YearlyParentSummaryRecordDTO> result = new ArrayList<>();
        if (sorted.size() <= 9) {
            result.addAll(sorted);
        } else {
            result.addAll(sorted.subList(0, 9));
            BigDecimal otherNetIncome = sorted.subList(9, sorted.size()).stream()
                    .map(YearlyParentSummaryRecordDTO::getNetIncome)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            YearlyParentSummaryRecordDTO othersSummary = createOthersSummary(year, type, otherNetIncome);
            result.add(othersSummary);
        }
        return result;
    }
    
    public String checkParentExists(Property.PropertyType type, String parentId) {
        if (type.equals(Property.PropertyType.GROUPED)) {
            Group group = groupRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group with ID: " + parentId + " not found"));
            return group.getName();
        }
        Property property = propertyRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with ID: " + parentId + " not found"));
        return property.getName();
    }
    
    public BigDecimal sumAmounts(Transaction.TransactionType type, List<Transaction> transactions) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(type)) {
                total = total.add(transaction.getAmount());
            }
        }
        return total;
    }
    
    public void calculateAmounts(Record record) {
        BigDecimal totalIncome = sumAmounts(Transaction.TransactionType.INCOME, record.getTransactions());
        BigDecimal totalExpense = sumAmounts(Transaction.TransactionType.EXPENSE, record.getTransactions());
        BigDecimal netIncome = totalIncome.subtract(totalExpense);
        record.setTotalIncome(totalIncome);
        record.setTotalExpense(totalExpense);
        record.setNetIncome(netIncome);
    }
    
    public Map<Integer, MonthlySummaryRecordDTO> getEmptyMonthlySummary(Integer year) {
        Map<Integer, MonthlySummaryRecordDTO> summaryMap = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            MonthlySummaryRecordDTO summary = new MonthlySummaryRecordDTO();
            summary.setMonth(month);
            summary.setYear(year);
            summary.setTotalIncome(BigDecimal.ZERO);
            summary.setTotalExpense(BigDecimal.ZERO);
            summary.setNetIncome(BigDecimal.ZERO);
            summaryMap.put(month, summary);
        }
        return summaryMap;
    }
    
    public YearlyParentSummaryRecordDTO createOthersSummary(Integer year, Property.PropertyType type, BigDecimal netIncome) {
        YearlyParentSummaryRecordDTO othersSummary = new YearlyParentSummaryRecordDTO();
        othersSummary.setYear(year);
        othersSummary.setParentType(type);
        othersSummary.setParentId(null);
        othersSummary.setParentName("Otros");
        othersSummary.setNetIncome(netIncome);
        return othersSummary;
    }
    
}
