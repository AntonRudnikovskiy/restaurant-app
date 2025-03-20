package cloud.java.model.converter;

import cloud.java.exception.OrderServiceException;
import cloud.java.model.MenuLineItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

@ReadingConverter
@RequiredArgsConstructor
public class MenuLineItemCollectionReadConverter implements Converter<Json, List<MenuLineItem>> {
    private final ObjectMapper objectMapper;

    @Override
    public List<MenuLineItem> convert(Json value) {
        try {
            return objectMapper.readValue(value.asArray(), new TypeReference<List<MenuLineItem>>() {});

        } catch (IOException e) {
            var msg = String.format("Failed to convert JSON %s to MenuLineItemCollection", value.asString());
            throw new OrderServiceException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
