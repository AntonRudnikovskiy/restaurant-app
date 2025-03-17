package cloud.java.menu.service;

import cloud.java.menu.TestData;
import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.exception.MenuServiceException;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.Ingredient;
import cloud.java.menu.model.IngredientCollection;
import cloud.java.menu.repositories.MenuItemRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class MenuServiceImplTest extends BaseTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuItemRepository repository;

    @Test
    void getMenusFor_DRINKS_returnsCorrectList() {
        List<MenuItemDto> drinks = menuService.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItemDto::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void createMenuItem_createsMenuItem() {
        var dto = TestData.createMenuRequest();
        var now = LocalDateTime.now().minusNanos(1000);
        MenuItemDto result = menuService.createMenuItem(dto);
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, dto, "name", "description", "price", "imageUrl", "timeToCook");
        assertThat(result.getCreatedAt()).isAfter(now);
        assertThat(result.getUpdatedAt()).isAfter(now);
    }

    @Test
    void createMenuItem_NameIsAlreadyExist() {
        CreateMenuRequest dto = CreateMenuRequest.builder()
                .name("Cappuccino")
                .price(BigDecimal.valueOf(100.01))
                .timeToCook(2000L)
                .category(Category.BREAKFAST)
                .weight(20.0F)
                .ingredientCollection(IngredientCollection.builder()
                        .ingredients(List.of(new Ingredient("coffie", 100)))
                        .build())
                .description("Cappuccino Description")
                .imageUrl("http://images.com/new_cappuccino.png")
                .build();

        assertThrows(MenuServiceException.class,
                () -> menuService.createMenuItem(dto));
    }

    @Test
    void deleteMenuItem() {
        menuService.deleteMenuItem(1L);
        assertNull(repository.findById(1L).orElse(null));
    }

    @Disabled
    @Test
    void getMenuItemById() {
        MenuItemDto menu = menuService.getMenu(1L);
        assertNotNull(menu);
    }

    @Test
    void getMenuItem_NonExistId() {
        assertThrows(MenuServiceException.class,
                () -> menuService.getMenu(10000L));
    }

    @Disabled
    @Test
    void updateMenuItem() {
        UpdateMenuRequest dto = TestData.updateMenuFullRequest();
        MenuItemDto update = menuService.updateMenuItem(2L, dto);
        assertFieldsEquality(update, dto, "name", "description", "price", "imageUrl", "timeToCook");
    }

    @Test
    void updateMenuItem_NonExist() {
        UpdateMenuRequest dto = TestData.updateMenuFullRequest();
        assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(1L, dto));
    }

    @Test
    void updateMenuItem_NameIsAlreadyExist() {
        UpdateMenuRequest dto = UpdateMenuRequest.builder()
                .name("Cappuccino")
                .price(BigDecimal.valueOf(100.01))
                .timeToCook(2000L)
                .description("Cappuccino Description")
                .imageUrl("http://images.com/new_cappuccino.png")
                .build();

        assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(1L, dto));
    }
}