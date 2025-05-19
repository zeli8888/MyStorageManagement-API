package ze.mystoragemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * @Author : Ze Li
 * @Date : 19/05/2025 19:13
 * @Version : V1.0
 * @Description :
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DishNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 179856888745840942L;

    public DishNotFoundException(String DishName) {
        super("Dish not found : " + DishName);
    }
}