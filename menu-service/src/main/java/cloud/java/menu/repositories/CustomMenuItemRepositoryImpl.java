package cloud.java.menu.repositories;

import cloud.java.menu.dto.SortBy;
import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.Category;
import cloud.java.menu.model.MenuItem;
import cloud.java.menu.model.MenuItem_;
import cloud.java.menu.repositories.updaters.MenuAttrUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomMenuItemRepositoryImpl implements CustomMenuItemRepository {
    private final EntityManager entityManager;
    private final List<MenuAttrUpdater<?>> updaters;

    @Override
    @Transactional
    public int updateMenu(Long id, UpdateMenuRequest dto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<MenuItem> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(MenuItem.class);
        Root<MenuItem> root = criteriaUpdate.from(MenuItem.class);

        updaters.forEach(updater -> updater.updateAttr(criteriaUpdate, dto));

        criteriaUpdate.where(criteriaBuilder.equal(root.get(MenuItem_.id), id));
        return entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public List<MenuItem> getMenusFor(Category category, SortBy sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MenuItem> criteriaQuery = criteriaBuilder.createQuery(MenuItem.class);
        Root<MenuItem> root = criteriaQuery.from(MenuItem.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get(MenuItem_.category), category))
                .orderBy(sortBy.getOrder(criteriaBuilder, root));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
