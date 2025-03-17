package cloud.java.menu.dto;

import cloud.java.menu.model.Category;
import cloud.java.menu.model.IngredientCollection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CreateMenuRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private long timeToCook;
    private double weight;
    private String imageUrl;
    private IngredientCollection ingredientCollection;
}
