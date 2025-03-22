package cloud.java.menu.repositories;

import cloud.java.menu.model.MenuItem;
import cloud.java.menu.model.projection.MenuItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, CustomMenuItemRepository {

    @Query("""
            select new cloud.java.menu.model.projection.MenuItemProjection(
            m.name,
            m.price
            ) from MenuItem m
            where m.name in :names
             """)
    List<MenuItemProjection> getMenuInfoForNames(@Param("names") Set<String> names);
}
