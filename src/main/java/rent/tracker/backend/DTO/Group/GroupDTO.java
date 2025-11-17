package rent.tracker.backend.DTO.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rent.tracker.backend.Model.Property;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String id;
    private String name;
    private String description;
    private List<Property> properties;
    
}
