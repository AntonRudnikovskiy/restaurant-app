package cloud.java.service;


import cloud.java.BaseIntegrationTest;
import cloud.java.TestConstants;
import cloud.java.TestDataProvider;
import cloud.java.dto.OrderResponse;
import cloud.java.dto.SortBy;
import cloud.java.exception.OrderServiceException;
import cloud.java.model.MenuLineItem;
import cloud.java.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import static cloud.java.TestConstants.*;
import static cloud.java.TestDataProvider.existingItems;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuOrderServiceTest extends BaseIntegrationTest {
    @Autowired
    private MenuOrderService menuOrderService;

    @Test
    void getOrdersOfUser_returnsCorrectFluxWhenUserHasOrders() {
        Flux<OrderResponse> orders = menuOrderService.getOrdersOfUser(USERNAME_ONE, SortBy.DATE_ASC, 0, 10);
        StepVerifier.create(orders)
                .expectNextMatches(order -> {
                    return assertOrder(order, ORDER_ONE_DATE);
                })
                .expectNextMatches(order -> {
                    return assertOrder(order, ORDER_TWO_DATE);
                })
                .expectNextMatches(order -> {
                    return assertOrder(order, ORDER_THREE_DATE);
                })
                .verifyComplete();
    }

    @Test
    void getOrdersOfUser_returnsCorrectFluxWhenUserHasNoOrders() {
        Flux<OrderResponse> orders = menuOrderService.getOrdersOfUser("user", SortBy.DATE_ASC, 0, 10);
        StepVerifier.create(orders)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createOrder_createsOrderWhenAllMenusAreAvailable() {
        prepareStubForSuccess();

        var createOrderRequest = TestDataProvider.createOrderRequest();
        var now = LocalDateTime.now().minusNanos(1000);
        Mono<OrderResponse> response = menuOrderService.createOrder(createOrderRequest, USERNAME_ONE);
        StepVerifier.create(response)
                .expectNextMatches(orderResponse -> {
                    assertThat(orderResponse.getAddress()).isEqualTo(createOrderRequest.getAddress());
                    assertThat(orderResponse.getTotalPrice()).isEqualTo(TestConstants.SUCCESS_TOTAL_PRICE);
                    assertThat(orderResponse.getStatus()).isEqualTo(OrderStatus.NEW);
                    assertThat(orderResponse.getCreatedAt()).isAfter(now);
                    var menuItems = new ArrayList<>(orderResponse.getMenuLineItems());
                    menuItems.sort(Comparator.comparing(MenuLineItem::getPrice));
                    assertThat(menuItems)
                            .map(MenuLineItem::getMenuItemName)
                            .containsExactly(MENU_ONE, MENU_TWO, MENU_THREE);
                    assertThat(menuItems)
                            .map(MenuLineItem::getQuantity)
                            .containsExactly(MENU_CREATE_ONE_QUANTITY, MENU_CREATE_TWO_QUANTITY, MENU_CREATE_THREE_QUANTITY);
                    assertThat(menuItems)
                            .map(MenuLineItem::getPrice)
                            .containsExactly(MENU_CREATE_ONE_PRICE, MENU_CREATE_TWO_PRICE, MENU_CREATE_THREE_PRICE);
                    return orderResponse.getOrderId() != null;
                })
                .verifyComplete();

        wiremock.verify(1, postRequestedFor(urlEqualTo(MENU_INFO_PATH)));
    }

    @Test
    void createOrder_createsOrderWhenMenuServiceNotAvailable() {
        prepareStubForServiceUnavailable();

        var createOrderRequest = TestDataProvider.createOrderRequest();
        Mono<OrderResponse> response = menuOrderService.createOrder(createOrderRequest, USERNAME_ONE);
        StepVerifier.create(response)
                .expectError(OrderServiceException.class)
                .verify();
        wiremock.verify(6, postRequestedFor(urlEqualTo(MENU_INFO_PATH)));
    }

    @Test
    void createOrder_createsOrderWhenMenusArePartialAvailable() {
        prepareStubForPartialSuccess();

        var createOrderRequest = TestDataProvider.createOrderRequest();
        Mono<OrderResponse> response = menuOrderService.createOrder(createOrderRequest, USERNAME_ONE);
        StepVerifier.create(response)
                .expectError(OrderServiceException.class)
                .verify();
        wiremock.verify(1, postRequestedFor(urlEqualTo(MENU_INFO_PATH)));
    }

    @Test
    void createOrder_createsOrderWhenTimeout() {
        prepareStubForSuccessWithTimeout();

        var createOrderRequest = TestDataProvider.createOrderRequest();
        Mono<OrderResponse> response = menuOrderService.createOrder(createOrderRequest, USERNAME_ONE);
        StepVerifier.create(response)
                .expectError(OrderServiceException.class)
                .verify();
        wiremock.verify(6, postRequestedFor(urlEqualTo(MENU_INFO_PATH)));
    }

    private boolean assertOrder(OrderResponse order, LocalDateTime createdAt) {
        return order.getOrderId() != null &&
                order.getAddress().getCity().equals(CITY_ONE) &&
                order.getAddress().getStreet().equals(STREET_ONE) &&
                order.getStatus().equals(OrderStatus.NEW) &&
                order.getCreatedAt().equals(createdAt) &&
                order.getMenuLineItems().equals(existingItems());
    }
}
