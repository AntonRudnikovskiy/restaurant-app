package cloud.java.menu.dto;

import cloud.java.menu.model.Category;
import cloud.java.menu.model.IngredientCollection;
import cloud.java.menu.validation.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateMenuRequest {
    @NullOrNotBlank(message = "Название не должно быть пустым.")
    private String name;
    @NullOrNotBlank(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull(message = "Цена не должна быть null.")
    @Positive(message = "Цена должна быть > 0.")
    private BigDecimal price;
    @NotNull(message = "Категория не должна быть null.")
    private Category category;
    @Positive(message = "Время приготовления должно быть > 0.")
    private long timeToCook;
    @Positive(message = "Вес должен быть > 0.")
    private double weight;
    @NullOrNotBlank(message = "Ссылка на фото не должна быть пустой.")
    private String imageUrl;
    @NotNull(message = "Ингредиенты не должны быть null.")
    private IngredientCollection ingredientCollection;
}
