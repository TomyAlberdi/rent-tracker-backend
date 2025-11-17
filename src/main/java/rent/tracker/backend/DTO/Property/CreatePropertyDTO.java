package rent.tracker.backend.DTO.Property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rent.tracker.backend.Model.Property;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePropertyDTO {
    private String name;
    private String description;
    private Property.PropertyType type;
    private String groupId;
}
