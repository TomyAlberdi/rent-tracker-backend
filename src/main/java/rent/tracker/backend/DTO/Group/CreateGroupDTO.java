package rent.tracker.backend.DTO.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupDTO {
    private String name;
    private String description;
}
