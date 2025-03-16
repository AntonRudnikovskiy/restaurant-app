package cloud.java.menu.service;

import cloud.java.menu.dto.UpdateMenuRequest;
import cloud.java.menu.model.MenuItem;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class MenuAttrUpdater<V> {
    private final SingularAttribute<MenuItem, V> attribute;
    private final Function<UpdateMenuRequest, V> getter;

    public void updateAttr(CriteriaUpdate<MenuItem> criteria, UpdateMenuRequest dto) {
        V value = getter.apply(dto);
        if (value != null) {
            criteria.set(attribute, value);
        }
    }
}

