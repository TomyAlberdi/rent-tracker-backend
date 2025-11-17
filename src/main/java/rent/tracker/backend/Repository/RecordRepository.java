package rent.tracker.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rent.tracker.backend.Model.Property;
import rent.tracker.backend.Model.Record;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends MongoRepository<Record, String> {
    List<Record> findAllByParentIdAndYear(String parentId, Integer year);
    Optional<Record> findByTypeAndParentIdAndMonthAndYear(Property.PropertyType type, String parentId, Integer month, Integer year);
    List<Record> findAllByYearOrderByMonth(Integer year);
    List<Record> findAllByYearAndTypeOrderByParentName(Integer year, Property.PropertyType type);
    List<Record> findAllByParentId(String parentId);
    void deleteAllByParentId(String parentId);
}
