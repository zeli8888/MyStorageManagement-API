package ze.mystoragemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredientId that = (DishIngredientId) o;
        return Objects.equals(dishId, that.dishId) &&
                Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId, ingredientId);
    }
}