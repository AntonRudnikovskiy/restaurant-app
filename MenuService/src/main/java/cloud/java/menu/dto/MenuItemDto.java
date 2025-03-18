package cloud.java.menu.dto;

import cloud.java.menu.model.Category;
import cloud.java.menu.model.IngredientCollection;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private long timeToCook;
    private double weight;
    private String imageUrl;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private IngredientCollection ingredientCollection;
}
