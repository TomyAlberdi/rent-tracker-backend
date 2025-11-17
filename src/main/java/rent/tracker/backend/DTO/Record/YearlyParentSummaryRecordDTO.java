package rent.tracker.backend.DTO.Record;

import lombok.*;
import rent.tracker.backend.Model.Property;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class YearlyParentSummaryRecordDTO {
    private Integer year;
    private BigDecimal netIncome;
    private Property.PropertyType parentType;
    private String parentId;
    private String parentName;
}
