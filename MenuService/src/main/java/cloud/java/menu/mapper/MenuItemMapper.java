package cloud.java.menu.mapper;


import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MenuItemMapper {
    MenuItemDto toDto(MenuItem domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MenuItem toDomain(CreateMenuRequest dto);

    List<MenuItemDto> toDtoList(List<MenuItem> domains);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "weight", ignore = true)
    MenuItem toEntity(UpdateMenuRequest updateMenuRequest);
}
