package rent.tracker.backend.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "records")
@CompoundIndex(name = "unique_record",
        def = "{'parentId' : 1, 'type': 1, 'month': 1, 'year': 1}",
        unique = true)
public class Record {
    @Id
    private String id;
    private Property.PropertyType type;
    private String parentId;
    private String parentName;
    private Integer month;
    private Integer year;
    private List<Transaction> transactions;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netIncome;
}
