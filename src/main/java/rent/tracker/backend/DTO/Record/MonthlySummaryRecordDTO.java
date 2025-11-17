package rent.tracker.backend.DTO.Record;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonthlySummaryRecordDTO {
    private Integer month;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netIncome;
}
