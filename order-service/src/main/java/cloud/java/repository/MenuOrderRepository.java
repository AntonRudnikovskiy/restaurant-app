package cloud.java.repository;

import cloud.java.model.MenuOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MenuOrderRepository extends ReactiveCrudRepository<MenuOrder, Long> {
    Flux<MenuOrder> findAllByCreatedBy(String username, Pageable pageable);
}
