package cloud.java.menu.repositories.custom;

import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.MenuItem;
import cloud.java.menu.repositories.MenuItemRepository;
import cloud.java.menu.service.MenuAttrUpdaters;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@DataJpaTest
@Import({MenuAttrUpdaters.class})
@Transactional(propagation = Propagation.NEVER)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(
                scripts = "classpath:db/migration/insert-menu.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = "classpath:db/migration/clear-menus.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
public class MenuItemRepositoryImplTest {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private EntityManager em;

    @Test
    void updateMenu_updatesMenu_whenSomeUpdateFieldsAreSet() {
        var dto = TestData.updateMenuFullRequest();
        var id = getIdByName("Cappuccino");
        int updateCount = menuItemRepository.updateMenu(id, dto);
        assertThat(updateCount).isEqualTo(1);
        MenuItem updated = menuItemRepository.findById(id).get();
        assertFieldsEquality(updated, dto, "name", "description", "price", "timeToCook", "imageUrl");
    }

    @Disabled
    @Test
    void updateMenu_throws_whenUpdateRequestHasNotUniqueName() {
        UpdateMenuRequest dto = UpdateMenuRequest.builder()
                .name("Cappuccino")
                .price(BigDecimal.valueOf(100.01))
                .timeToCook(2000L)
                .description("New Cappuccino Description")
                .imageUrl("http://images.com/new_cappuccino.png")
                .build();
        var id = getIdByName("Cappuccino");
        assertThrows(DataIntegrityViolationException.class,
                () -> menuItemRepository.updateMenu(id, dto));
    }

    @Test
    void updateMenu_updatesNothing_whenNoMenuPresentInDB() {
        var dto = TestData.updateMenuFullRequest();
        int updateCount = menuItemRepository.updateMenu(1000L, dto);
        assertThat(updateCount).isEqualTo(0);
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.PRICE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.PRICE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.ZA);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Wine", "Tea", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }

    private Long getIdByName(String name) {
        return em.createQuery("select m.id from MenuItem m where m.name= ?1", Long.class)
                .setParameter(1, name)
                .getSingleResult();
    }

    private <T, R> void assertFieldsEquality(T item, R dto, String... fields) {
        assertFieldsExistence(item, dto, fields);
        assertThat(item).usingRecursiveComparison()
                .comparingOnlyFields(fields)
                .isEqualTo(dto);
    }

    private <T, R> void assertElementsInOrder(List<T> items, Function<T, R> mapper, List<R> expectedElements) {
        var actualNames = items.stream().map(mapper).toList();
        assertThat(actualNames).containsExactlyElementsOf(expectedElements);
    }

    private <T, R> void assertFieldsExistence(T item, R dto, String... fields) {
        boolean itemFieldsMissing = Arrays.stream(fields)
                .anyMatch(field -> getField(item, field) == null);
        boolean dtoFieldsMissing = Arrays.stream(fields)
                .anyMatch(field -> getField(dto, field) == null);

        if (itemFieldsMissing || dtoFieldsMissing) {
            throw new AssertionError("One or more fields do not exist in the provided objects. Actual: %s. Expected: %s. Fields to compare: %s"
                    .formatted(item, dto, List.of(fields)));
        }
    }

    private <T> Field getField(T item, String fieldName) {
        try {
            return item.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}