package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 12:44
 * @Version : V1.0
 * @Description :
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(unique = true, nullable = false)
    private String ingredientName;

    private Long ingredientStorage;

    private Long ingredientCost;

    private String ingredientDesc;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<DishIngredient> dishIngredients = new HashSet<>();

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<DishRecordIngredient> dishRecordIngredients = new HashSet<>();

    public Ingredient(Long ingredientId, String ingredientName, Long ingredientStorage, Long ingredientCost, String ingredientDesc, Set<DishIngredient> dishIngredients, Set<DishRecordIngredient> dishRecordIngredients) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientStorage = ingredientStorage;
        this.ingredientCost = ingredientCost;
        this.ingredientDesc = ingredientDesc;
        this.dishIngredients = dishIngredients;
        this.dishRecordIngredients = dishRecordIngredients;
    }

    public Ingredient(Long ingredientId, String ingredientName, Long ingredientCost, Long ingredientStorage, String ingredientDesc) {
        this.ingredientDesc = ingredientDesc;
        this.ingredientCost = ingredientCost;
        this.ingredientStorage = ingredientStorage;
        this.ingredientName = ingredientName;
        this.ingredientId = ingredientId;
    }

    public Set<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(Set<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public Set<DishRecordIngredient> getDishRecordIngredients() {
        return dishRecordIngredients;
    }

    public void setDishRecordIngredients(Set<DishRecordIngredient> dishRecordIngredients) {
        this.dishRecordIngredients = dishRecordIngredients;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Long getIngredientStorage() {
        return ingredientStorage;
    }

    public void setIngredientStorage(Long ingredientStorage) {
        this.ingredientStorage = ingredientStorage;
    }

    public Long getIngredientCost() {
        return ingredientCost;
    }

    public void setIngredientCost(Long ingredientCost) {
        this.ingredientCost = ingredientCost;
    }

    public String getIngredientDesc() {
        return ingredientDesc;
    }

    public void setIngredientDesc(String ingredientDesc) {
        this.ingredientDesc = ingredientDesc;
    }

    public Ingredient() {
    }

}
