package cloud.java.menu.dto;

import cloud.java.menu.exception.MenuServiceException;
import cloud.java.menu.model.MenuItem;
import cloud.java.menu.model.MenuItem_;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;

public enum SortBy {
    AZ {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.asc(root.get(MenuItem_.name));
        }
    },
    ZA {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.desc(root.get(MenuItem_.name));
        }
    },
    PRICE_ASC {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.asc(root.get(MenuItem_.price));
        }
    },
    PRICE_DESC {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.desc(root.get(MenuItem_.price));
        }
    },
    DATE_ASC {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.asc(root.get(MenuItem_.createdAt));
        }
    },
    DATE_DESC {
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<MenuItem> root) {
            return cb.desc(root.get(MenuItem_.createdAt));
        }
    };

    public abstract Order getOrder(CriteriaBuilder cb, Root<MenuItem> root);

    @JsonCreator
    public static SortBy fromString(String str) {
        try {
            return SortBy.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to create SortBy from string: %s".formatted(str);
            throw new MenuServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
