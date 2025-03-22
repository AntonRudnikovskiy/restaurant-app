package cloud.java.service;

import cloud.java.dto.CreateOrderRequest;
import cloud.java.dto.OrderResponse;
import cloud.java.dto.SortBy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMenuOrderService {
    Mono<OrderResponse> createOrder(CreateOrderRequest request, String username);

    Flux<OrderResponse> getOrdersOfUser(String username, SortBy sortBy, int from, int size);
}
