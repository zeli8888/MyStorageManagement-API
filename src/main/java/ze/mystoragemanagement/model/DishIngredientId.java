package ze.mystoragemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 23:26
 * @Version : V1.0
 * @Description :
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishIngredientId implements Serializable {
    @Column(name = "dish_id")
    private Long dishId;
    @Column(name = "ingredient_id")
    private Long ingredientId;
}