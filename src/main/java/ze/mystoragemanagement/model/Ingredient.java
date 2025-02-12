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

    @Column(unique = true)
    private String ingredientName;

    private Long ingredientStorage;

    private Long ingredientCost;

    private String ingredientDesc;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "dishIngredients", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JsonIgnore
    private Set<Dish> dishes = new HashSet<>();

    public Set<Dish> getDishes() {
        return dishes;
    }
}
