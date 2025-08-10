package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 13:10
 * @Version : V1.0
 * @Description :
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DishRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.DishRecordView.class)
    private Long dishRecordId;

    @JsonView(Views.DishRecordView.class)
    private ZonedDateTime dishRecordTime;

    @JsonView(Views.DishRecordView.class)
    private String dishRecordDesc;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "dish_name", nullable = true)
    @JsonView(Views.DishRecordView.class)
    private Dish dish;

    @OneToMany(mappedBy = "dishRecord", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.DishRecordView.class)
    private Set<DishRecordIngredient> dishRecordIngredients = new HashSet<>();

    @Column(nullable = false)
    @JsonIgnore
    private String firebaseId;
}
