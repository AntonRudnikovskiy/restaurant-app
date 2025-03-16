package cloud.java.menu.repositories;

import cloud.java.menu.model.MenuItem;
import cloud.java.menu.repositories.custom.CustomMenuItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, CustomMenuItemRepository {
}
