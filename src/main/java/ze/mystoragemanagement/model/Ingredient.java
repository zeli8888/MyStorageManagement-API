package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 12:44
 * @Version : V1.0
 * @Description :
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "ingredient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_ingredient_per_user",
                        columnNames = {"firebaseId", "ingredientName"}
                )
        }
)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.DishRecordView.class)
    private Long ingredientId;

    @Column(nullable = false)
    @JsonView(Views.DishRecordView.class)
    private String ingredientName;

    @JsonView(Views.DishRecordView.class)
    private Long ingredientStorage;

    @JsonView(Views.DishRecordView.class)
    private Long ingredientCost;

    @JsonView(Views.DishRecordView.class)
    private String ingredientDesc;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private Set<DishIngredient> dishIngredients = new HashSet<>();

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private Set<DishRecordIngredient> dishRecordIngredients = new HashSet<>();

    @Column(nullable = false)
    @JsonIgnore
    private String firebaseId;
}
