package cloud.java.menu.controller;

import cloud.java.menu.dto.MenuItemDto;
import cloud.java.menu.service.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static cloud.java.menu.TestConstants.BASE_URL;
import static cloud.java.menu.TestData.createMenuRequest;
import static cloud.java.menu.TestData.updateMenuFullRequest;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuItemControllerTest extends BaseTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createMenuItem_createsItem() {
        var dto = createMenuRequest();
        var now = LocalDateTime.now();

        webTestClient.post()
                .uri(BASE_URL + "/")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo(dto.getName());
                    assertThat(response.getDescription()).isEqualTo(dto.getDescription());
                    assertThat(response.getPrice()).isEqualTo(dto.getPrice());
                    assertThat(response.getTimeToCook()).isEqualTo(dto.getTimeToCook());
                    assertThat(response.getImageUrl()).isEqualTo(dto.getImageUrl());
                    assertThat(response.getIngredientCollection()).isEqualTo(dto.getIngredientCollection());
                    assertThat(response.getCreatedAt()).isAfter(now);
                    assertThat(response.getUpdatedAt()).isAfter(now);
                });
    }

    @Test
    void createMenuItem_returnsConflict_whenMenuWithThatNameInDb() {
        var dto = createMenuRequest();
        dto.setName("Cappuccino");

        webTestClient.post()
                .uri(BASE_URL + "/")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateMenuItem_updatesItem() {
        var update = updateMenuFullRequest();
        var id = getIdByName("Cappuccino");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getName()).isEqualTo(update.getName());
                    assertThat(response.getPrice()).isEqualTo(update.getPrice());
                    assertThat(response.getTimeToCook()).isEqualTo(update.getTimeToCook());
                    assertThat(response.getDescription()).isEqualTo(update.getDescription());
                    assertThat(response.getImageUrl()).isEqualTo(update.getImageUrl());
                });
    }

    @Test
    void updateMenuItem_returnsNotFound_whenItemNotInDb() {
        var update = updateMenuFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMenuItem() {
        var id = getIdByName("Cappuccino");
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Cappuccino");
                });
    }

    @Test
    void getMenuItem_NotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + 100000L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMenusFor() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/")
                        .queryParam("category", "drinks")
                        .queryParam("sort")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(items -> {
                    assertThat(items).hasSize(3);
                    assertThat(items.get(0).getName()).isEqualTo("Cappuccino");
                    assertThat(items.get(1).getName()).isEqualTo("Tea");
                    assertThat(items.get(2).getName()).isEqualTo("Wine");
                });
    }

    @Test
    void getMenusFor_EmptyCategory() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/")
                        .queryParam("category", "lunch")
                        .queryParam("sort", "az")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(response -> {
                    assertThat(response).isEmpty();
                });
    }
}
