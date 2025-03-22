package cloud.java.menu.controller;

import cloud.java.menu.dto.CreateMenuRequest;
import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.dto.OrderMenuRequest;
import cloud.java.menu.dto.OrderMenuResponse;
import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;
import cloud.java.menu.service.IMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "MenuItemController", description = "REST API для работы с меню.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/menu-item")
public class MenuItemController {
    private final IMenuService menuService;

    @Operation(
            summary = "${api.menu-create.summary}",
            description = "${api.menu-create.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "${api.response.createOk}"),
            @ApiResponse(
                    responseCode = "409",
                    description = "${api.response.createConflict}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.createBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )),
    })
    @PostMapping("/")
    public ResponseEntity<MenuItemDto> createMenuItem(@RequestBody @Valid CreateMenuRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuService.createMenuItem(request));
    }

    @Operation(
            summary = "${api.menu-update.summary}",
            description = "${api.menu-update.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.updateOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.updateBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMenuRequest update) {
        return ResponseEntity.ok().body(menuService.updateMenuItem(id, update));
    }

    @Operation(
            summary = "${api.menu-delete.summary}",
            description = "${api.menu-delete.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "${api.response.deleteNoContent}")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MenuItemDto> deleteMenuItem(@PathVariable("id") Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "${api.menu-get.summary}",
            description = "${api.menu-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDto> getMenuItem(@PathVariable("id") Long id) {
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @Operation(
            summary = "${api.menu-list-get.summary}",
            description = "${api.menu-list-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getListOk}"),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.getListBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/")
    public ResponseEntity<List<MenuItemDto>> getMenusFor(
            @RequestParam("category") String category,
            @RequestParam(value = "sort", defaultValue = "az") String sortBy) {
        return ResponseEntity.ok(menuService.getMenusFor(Category.fromString(category), SortBy.fromString(sortBy)));
    }

    @Operation(
            summary = "${api.menu-info.summary}",
            description = "${api.menu-info.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getMenuInfoOk}"),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.getMenuInfoBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping("/menu-info")
    public OrderMenuResponse getMenusForOrder(@RequestBody @Valid OrderMenuRequest request) {
        log.info("Received request to GET info for menu with names: {}", request.getMenuNames());
        return menuService.getMenusForOrder(request);
    }
}
