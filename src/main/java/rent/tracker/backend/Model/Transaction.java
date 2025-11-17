package rent.tracker.backend.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    private String title;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private String metaPropertyId;
    
    public enum TransactionType {
        INCOME,
        EXPENSE
    }
    
}
