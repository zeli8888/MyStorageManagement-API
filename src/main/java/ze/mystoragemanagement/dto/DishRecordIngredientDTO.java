package ze.mystoragemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ze.mystoragemanagement.model.DishRecord;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 22:42
 * @Version : V1.0
 * @Description :
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DishRecordIngredientDTO {
    private DishRecord dishRecord;
    private IngredientIdQuantityDTO[] ingredientIdQuantityList;
}
