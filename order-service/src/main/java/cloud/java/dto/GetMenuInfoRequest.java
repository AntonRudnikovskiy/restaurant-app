package cloud.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Data
@AllArgsConstructor
public class GetMenuInfoRequest {
    private Set<String> menuNames;
}
