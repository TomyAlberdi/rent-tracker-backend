package rent.tracker.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rent.tracker.backend.Model.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
}
