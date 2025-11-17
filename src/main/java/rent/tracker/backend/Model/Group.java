package rent.tracker.backend.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "groups")
public class Group {
    
    @Id
    private String id;
    @NotBlank
    private String name;
    private String description;
}
