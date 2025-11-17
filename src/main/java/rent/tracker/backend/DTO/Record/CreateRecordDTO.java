package rent.tracker.backend.DTO.Record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rent.tracker.backend.Model.Property;
import rent.tracker.backend.Model.Transaction;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordDTO {
    
    private String id;
    private Property.PropertyType type;
    private String parentId;
    private Integer month;
    private Integer year;
    private List<Transaction> transactions;
    
}
