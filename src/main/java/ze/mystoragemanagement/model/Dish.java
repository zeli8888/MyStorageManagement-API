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
 * @Date : 12/02/2025 12:40
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
        name = "dish",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_dish_per_user",
                        columnNames = {"firebaseId", "dishName"}
                )
        }
)
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.DishRecordView.class)
    private Long dishId;

    @Column(nullable = false)
    @JsonView(Views.DishRecordView.class)
    private String dishName;

    private String dishDesc;

    @OneToMany(mappedBy = "dish", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DishIngredient> dishIngredients = new HashSet<>();

    @OneToMany(mappedBy = "dish", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DishRecord> dishRecords = new HashSet<>();

    @Column(nullable = false)
    @JsonIgnore
    private String firebaseId;
}
