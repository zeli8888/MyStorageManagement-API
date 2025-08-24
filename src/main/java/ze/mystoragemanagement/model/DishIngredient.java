package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 21:52
 * @Version : V1.0
 * @Description :
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish_ingredient")
public class DishIngredient {
    @EmbeddedId
    @JsonIgnore
    private DishIngredientId dishIngredientId;
    private Double dishIngredientQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishId")
    @JoinColumn(name = "dish_id")
    @JsonIgnore
    private Dish dish;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}