package cloud.java.menu.service;

import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.MenuInfo;
import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.dto.OrderMenuRequest;
import cloud.java.menu.dto.OrderMenuResponse;
import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.exception.MenuServiceException;
import cloud.java.menu.mapper.MenuItemMapper;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.MenuItem;
import cloud.java.menu.model.projection.MenuItemProjection;
import cloud.java.menu.repositories.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService implements IMenuService {
    private final MenuItemRepository repository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        MenuItem menuItem = menuItemMapper.toDomain(dto);
        try {
            return menuItemMapper.toDto(repository.save(menuItem));
        } catch (DataIntegrityViolationException exception) {
            String message = String.format("Failed to create MenuItem: %s. Reason: Item with name %s already exists.", dto, dto.getName());
            throw new MenuServiceException(message, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteMenuItem(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update) {
        try {
            int updateCount = repository.updateMenu(id, update);
            if (updateCount == 0) {
                var msg = String.format("MenuItem with id=%d not found.", id);
                throw new MenuServiceException(msg, HttpStatus.NOT_FOUND);
            }
            return getMenu(id);
        } catch (DataIntegrityViolationException ex) {
            var msg = String.format("Failed to update MenuItem with ID: %d. Reason: Item with name %s already exists.",
                    id, update.getName());
            throw new MenuServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        return repository.findById(id)
                .map(menuItemMapper::toDto)
                .orElseThrow(() -> {
                    String message = String.format("MenuItem with id=%d not found.", id);
                    throw new MenuServiceException(message, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        return menuItemMapper.toDtoList(repository.getMenusFor(category, sortBy));
    }

    @Override
    public OrderMenuResponse getMenusForOrder(OrderMenuRequest request) {
        Map<String, MenuItemProjection> mapToProjection = repository.getMenuInfoForNames(request.getMenuNames()).stream()
                .collect(Collectors.toMap(MenuItemProjection::getName, Function.identity()));

        List<MenuInfo> menuInfos = new ArrayList<>();
        for (String menuItem : request.getMenuNames()) {
            MenuItemProjection projection = mapToProjection.get(menuItem);
            if (mapToProjection.containsKey(menuItem)) {
                menuInfos.add(new MenuInfo(projection.getName(), projection.getPrice(), true));
            } else {
                menuInfos.add(new MenuInfo(projection.getName(), projection.getPrice(), false));
            }
        }
        return OrderMenuResponse.builder().menuInfoList(menuInfos).build();
    }
}
