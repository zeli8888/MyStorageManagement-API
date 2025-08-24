package ze.mystoragemanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 22:55
 * @Version : V1.0
 * @Description :
 */
@Setter
@Getter
@NoArgsConstructor
public class IngredientIdQuantityDTO {
    private Long ingredientId;
    private Double quantity;
    private String ingredientName;

    public IngredientIdQuantityDTO(Double quantity, String ingredientName) {
        this.quantity = quantity;
        this.ingredientName = ingredientName;
    }

    public IngredientIdQuantityDTO(Long ingredientId, Double quantity) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

}
