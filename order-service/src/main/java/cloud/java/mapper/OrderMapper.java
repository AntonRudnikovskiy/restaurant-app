package cloud.java.mapper;

import cloud.java.dto.Address;
import cloud.java.dto.CreateOrderRequest;
import cloud.java.dto.GetMenuInfoResponse;
import cloud.java.dto.MenuInfo;
import cloud.java.dto.OrderResponse;
import cloud.java.exception.OrderServiceException;
import cloud.java.model.MenuLineItem;
import cloud.java.model.MenuOrder;
import cloud.java.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public MenuOrder mapToOrder(CreateOrderRequest request,
                                String username,
                                GetMenuInfoResponse infoResponse) {
        throwIfHasUnavailableMenuItems(infoResponse);
        return MenuOrder.builder()
                .apartment(request.getAddress().getApartment())
                .price(geTotalPrice(request, infoResponse))
                .menuLineItems(mapToMenuLineItem(infoResponse, request))
                .city(request.getAddress().getCity())
                .house(request.getAddress().getHouse())
                .street(request.getAddress().getStreet())
                .createdBy(username)
                .status(OrderStatus.NEW)
                .build();
    }

    public OrderResponse mapToResponse(MenuOrder order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getPrice())
                .address(Address.builder()
                        .city(order.getCity())
                        .apartment(order.getApartment())
                        .house(order.getHouse())
                        .street(order.getStreet())
                        .build())
                .menuLineItems(order.getMenuLineItems())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private void throwIfHasUnavailableMenuItems(GetMenuInfoResponse infoResponse) {
        boolean isAvailable = infoResponse.getMenuInfos().stream().allMatch(MenuInfo::getIsAvailable);
        if (!isAvailable) {
            String msg = String.format("Cannot create order, because some items are not available: %s",
                    infoResponse.getMenuInfos());
            throw new OrderServiceException(msg, HttpStatus.NOT_FOUND);
        }
    }

    private List<MenuLineItem> mapToMenuLineItem(GetMenuInfoResponse infoResponse, CreateOrderRequest request) {
        return infoResponse.getMenuInfos().stream()
                .map(menuInfo -> MenuLineItem.builder()
                        .menuItemName(menuInfo.getName())
                        .price(menuInfo.getPrice())
                        .quantity(request.getNameToQuantity().get(menuInfo.getName()))
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal geTotalPrice(CreateOrderRequest request, GetMenuInfoResponse infoResponse) {
        return infoResponse.getMenuInfos().stream()
                .map(info -> info.getPrice().multiply(BigDecimal.valueOf(request.getNameToQuantity().get(info.getName()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
