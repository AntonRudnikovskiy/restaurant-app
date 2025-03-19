package cloud.java.menu.model;

import cloud.java.menu.exception.MenuServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum Category {
    BREAKFAST,
    LUNCH,
    DINNER,
    DRINKS,
    SNACKS,
    SALADS;

    @JsonCreator
    public static Category fromString(String str) {
        try {
            return Category.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to create Category from string: %s".formatted(str);
            throw new MenuServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
