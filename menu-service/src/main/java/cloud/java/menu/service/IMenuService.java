package cloud.java.menu.service;

import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.dto.OrderMenuRequest;
import cloud.java.menu.dto.OrderMenuResponse;
import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;

import java.util.List;

public interface IMenuService {
    MenuItemDto createMenuItem(CreateMenuRequest dto);

    void deleteMenuItem(Long id);

    MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update);

    MenuItemDto getMenu(Long id);

    List<MenuItemDto> getMenusFor(Category category, SortBy sortBy);
    OrderMenuResponse getMenusForOrder(OrderMenuRequest request);
}
