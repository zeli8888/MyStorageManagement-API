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
 * @Date : 15/04/2025 12:14
 * @Version : V1.0
 * @Description :
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishRecordIngredientId implements Serializable {
    @Column(name = "dish_record_id")
    private Long dishRecordId;
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishRecordIngredientId that = (DishRecordIngredientId) o;
        return Objects.equals(dishRecordId, that.dishRecordId) &&
                Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishRecordId, ingredientId);
    }
}
