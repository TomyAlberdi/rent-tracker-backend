package rent.tracker.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rent.tracker.backend.Model.Property;
import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByGroupId(String groupId);
    List<Property> findByType(Property.PropertyType type);
}
