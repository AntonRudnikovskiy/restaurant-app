package cloud.java.service;

import cloud.java.client.MenuClient;
import cloud.java.dto.CreateOrderRequest;
import cloud.java.dto.GetMenuInfoRequest;
import cloud.java.dto.OrderResponse;
import cloud.java.dto.SortBy;
import cloud.java.mapper.OrderMapper;
import cloud.java.repository.MenuOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuOrderService implements IMenuOrderService {
    private final MenuOrderRepository repository;
    private final MenuClient menuClient;
    private final OrderMapper orderMapper;

    @Override
    public Mono<OrderResponse> createOrder(CreateOrderRequest request, String username) {
        Set<String> uniqueMenuNames = new HashSet<>(request.getNameToQuantity().keySet());
        return menuClient.getMenuInfo(new GetMenuInfoRequest(uniqueMenuNames))
                .map(response -> orderMapper.mapToOrder(request, username, response))
                .flatMap(repository::save)
                .map(orderMapper::mapToResponse);

    }

    @Override
    public Flux<OrderResponse> getOrdersOfUser(String username, SortBy sortBy, int from, int size) {
        return repository.findAllByCreatedBy(username, PageRequest.of(from, size, sortBy.getSort()))
                .map(orderMapper::mapToResponse);
    }
}
