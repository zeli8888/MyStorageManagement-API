package ze.mystoragemanagement.model;

import jakarta.persistence.Column;

import java.io.Serializable;

/**
 * @Author : Ze Li
 * @Date : 15/04/2025 12:14
 * @Version : V1.0
 * @Description :
 */
public class DishRecordIngredientId implements Serializable {
    @Column(name = "dish_record_id")
    private Long dishRecordId;
    @Column(name = "ingredient_id")
    private Long ingredientId;

    public Long getDishRecordId() {
        return dishRecordId;
    }

    public void setDishRecordId(Long dishRecordId) {
        this.dishRecordId = dishRecordId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public DishRecordIngredientId() {
    }

    public DishRecordIngredientId(Long ingredientId, Long dishRecordId) {
        this.ingredientId = ingredientId;
        this.dishRecordId = dishRecordId;
    }
}
