package cloud.java.menu.repositories;

import cloud.java.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, CustomMenuItemRepository {
    boolean existsByName(String name);
}
