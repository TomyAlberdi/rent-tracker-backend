package rent.tracker.backend.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "properties")
public class Property {
    @Id
    private String id;

    @NotBlank
    private String name;
    private String description;
    private PropertyType type;
    private String groupId;

    public enum PropertyType {
        INDIVIDUAL,
        GROUPED
    }
}
