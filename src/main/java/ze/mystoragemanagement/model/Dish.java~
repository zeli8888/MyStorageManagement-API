package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 12:40
 * @Version : V1.0
 * @Description :
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishId;

    @Column(unique = true, nullable = false)
    private String dishName;

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Dish(Long dishId, String dishName, String dishDesc, Set<DishIngredient> dishIngredients) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.dishDesc = dishDesc;
        this.dishIngredients = dishIngredients;
    }

    private String dishDesc;

//    @ManyToMany(fetch=FetchType.EAGER, cascade = {
//            CascadeType.PERSIST,
//            CascadeType.MERGE,
//            CascadeType.REFRESH
//    })
//    @JoinTable(name = "dish_ingredient",
//            joinColumns = @JoinColumn(name = "dish_id"),
//            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
//    private Set<Ingredient> dishIngredients = new HashSet<>();

    @OneToMany(mappedBy = "dish", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<DishIngredient> dishIngredients = new HashSet<>();

    public Dish() {
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishDesc() {
        return dishDesc;
    }

    public void setDishDesc(String dishDesc) {
        this.dishDesc = dishDesc;
    }

    public Set<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(Set<DishIngredient> ingredients) {
        this.dishIngredients = ingredients;
    }

    public Dish(Long dishId, String dishDesc, Set<DishIngredient> dishIngredients) {
        this.dishId = dishId;
        this.dishDesc = dishDesc;
        this.dishIngredients = dishIngredients;
    }
}
