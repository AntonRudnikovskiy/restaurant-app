package cloud.java.menu.repositories.custom;

import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.MenuItem;

import java.util.List;

public interface CustomMenuItemRepository {
    int updateMenu(Long id, UpdateMenuRequest dto);
    List<MenuItem> getMenusFor(Category category, SortBy sortBy);
}
