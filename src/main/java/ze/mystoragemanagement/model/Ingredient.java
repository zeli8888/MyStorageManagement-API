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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "dishIngredients", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JsonIgnore
    private Set<Dish> dishes = new HashSet<>();

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

    public void setDishes(Set<Dish> dishes) {
        this.dishes = dishes;
    }

    public Ingredient() {
    }

    public Ingredient(Long ingredientId, String ingredientName, Long ingredientStorage, Long ingredientCost, String ingredientDesc, Set<Dish> dishes) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientStorage = ingredientStorage;
        this.ingredientCost = ingredientCost;
        this.ingredientDesc = ingredientDesc;
        this.dishes = dishes;
    }

    public Set<Dish> getDishes() {
        return dishes;
    }
}
