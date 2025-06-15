package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

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

    public DishRecord() {
    }

    public DishRecord(Long dishRecordId, ZonedDateTime dishRecordTime, String dishRecordDesc, Dish dish, Set<DishRecordIngredient> dishRecordIngredients) {
        this.dishRecordId = dishRecordId;
        this.dishRecordTime = dishRecordTime;
        this.dishRecordDesc = dishRecordDesc;
        this.dish = dish;
        this.dishRecordIngredients = dishRecordIngredients;
    }


    public Set<DishRecordIngredient> getDishRecordIngredients() {
        return dishRecordIngredients;
    }

    public void setDishRecordIngredients(Set<DishRecordIngredient> dishRecordIngredients) {
        this.dishRecordIngredients = dishRecordIngredients;
    }

    public Long getDishRecordId() {
        return dishRecordId;
    }

    public void setDishRecordId(Long dishRecordId) {
        this.dishRecordId = dishRecordId;
    }

    public ZonedDateTime getDishRecordTime() {
        return dishRecordTime;
    }

    public void setDishRecordTime(ZonedDateTime dishRecordTime) {
        this.dishRecordTime = dishRecordTime;
    }

    public String getDishRecordDesc() {
        return dishRecordDesc;
    }

    public void setDishRecordDesc(String dishRecordDesc) {
        this.dishRecordDesc = dishRecordDesc;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dishName) {
        this.dish = dishName;
    }

}
