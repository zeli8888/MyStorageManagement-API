package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 21:52
 * @Version : V1.0
 * @Description :
 */

@Entity
@Table(name = "dish_ingredient")
public class DishIngredient {
    @EmbeddedId
    private DishIngredientId dishIngredientId;
    private Long dishIngredientQuantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade= {CascadeType.ALL})
    @MapsId("dishId")
    @JoinColumn(name = "dish_id")
    @JsonIgnore
    private Dish dish;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade= {CascadeType.ALL})
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    @JsonIgnore
    private Ingredient ingredient;

    public DishIngredientId getDishIngredientId() {
        return dishIngredientId;
    }

    public void setDishIngredientId(DishIngredientId dishIngredientId) {
        this.dishIngredientId = dishIngredientId;
    }

    public Long getDishIngredientQuantity() {
        return dishIngredientQuantity;
    }

    public void setDishIngredientQuantity(Long dishIngredientQuantity) {
        this.dishIngredientQuantity = dishIngredientQuantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public DishIngredient(DishIngredientId dishIngredientId, Long dishIngredientQuantity, Dish dish, Ingredient ingredient) {
        this.dishIngredientId = dishIngredientId;
        this.dishIngredientQuantity = dishIngredientQuantity;
        this.dish = dish;
        this.ingredient = ingredient;
    }

    public DishIngredient() {
    }
}