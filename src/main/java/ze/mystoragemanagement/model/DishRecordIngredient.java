package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * @Author : Ze Li
 * @Date : 15/04/2025 12:10
 * @Version : V1.0
 * @Description :
 */
@Entity
@Table(name = "dish_record_ingredient")
public class DishRecordIngredient {
    @EmbeddedId
    @JsonIgnore
    private DishRecordIngredientId dishRecordIngredientId;
    private Long dishRecordIngredientQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishRecordId")
    @JoinColumn(name = "dish_record_id")
    @JsonIgnore
    private DishRecord dishRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;


    public DishRecordIngredient() {
    }

    public DishRecordIngredient(DishRecordIngredientId dishRecordIngredientId, Long dishRecordIngredientQuantity, DishRecord dishRecord, Ingredient ingredient) {
        this.dishRecordIngredientId = dishRecordIngredientId;
        this.dishRecordIngredientQuantity = dishRecordIngredientQuantity;
        this.dishRecord = dishRecord;
        this.ingredient = ingredient;
    }

    public DishRecordIngredientId getDishRecordIngredientId() {
        return dishRecordIngredientId;
    }

    public void setDishRecordIngredientId(DishRecordIngredientId dishRecordIngredientId) {
        this.dishRecordIngredientId = dishRecordIngredientId;
    }

    public Long getDishRecordIngredientQuantity() {
        return dishRecordIngredientQuantity;
    }

    public void setDishRecordIngredientQuantity(Long dishRecordIngredientQuantity) {
        this.dishRecordIngredientQuantity = dishRecordIngredientQuantity;
    }

    public DishRecord getDishRecord() {
        return dishRecord;
    }

    public void setDishRecord(DishRecord dishRecord) {
        this.dishRecord = dishRecord;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

}
