package cloud.java.menu;

import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.Ingredient;
import cloud.java.menu.model.IngredientCollection;

import java.math.BigDecimal;
import java.util.List;

import static cloud.java.menu.TestConstants.*;

public class TestData {
    public static IngredientCollection italianSaladIngredients() {
        return new IngredientCollection(
                List.of(
                        new Ingredient(ITALIAN_SALAD_GREENS_INGREDIENT, ITALIAN_SALAD_GREENS_INGREDIENT_CALORIES),
                        new Ingredient(ITALIAN_SALAD_TOMATOES_INGREDIENT, ITALIAN_SALAD_TOMATOES_INGREDIENT_CALORIES)
                )
        );
    }

    public static CreateMenuRequest createMenuRequest() {
        return CreateMenuRequest.builder()
                .name("Old Cappuccino")
                .price(BigDecimal.valueOf(100.01))
                .timeToCook(2000L)
                .category(Category.BREAKFAST)
                .weight(20.0)
                .ingredientCollection(IngredientCollection.builder()
                        .ingredients(List.of(new Ingredient("coffe", 100)))
                        .build())
                .description("Old Cappuccino Description")
                .imageUrl("http://images.com/new_cappuccino.png")
                .build();
    }

    public static UpdateMenuRequest updateMenuFullRequest() {
        return UpdateMenuRequest.builder()
                .name("New Cappuccino")
                .price(BigDecimal.valueOf(100.01))
                .timeToCook(2000L)
                .description("New Cappuccino Description")
                .imageUrl("http://images.com/new_cappuccino.png")
                .build();
    }
}
