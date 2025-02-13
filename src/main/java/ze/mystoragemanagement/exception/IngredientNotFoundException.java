package ze.mystoragemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 22:54
 * @Version : V1.0
 * @Description :
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class IngredientNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 179856888745840942L;

    public IngredientNotFoundException(String ingredientName) {
        super("Ingredient not found : " + ingredientName);
    }
}
