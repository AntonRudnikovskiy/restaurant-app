package cloud.java.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuLineItem {
    private String menuItemName;
    private BigDecimal price;
    private Integer quantity;
}
