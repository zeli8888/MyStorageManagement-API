package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    private Long dishRecordId;

    private LocalDateTime dishRecordTime;

    private String dishRecordDesc;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "dish_name", nullable = false)
    private Dish dish;

    public Long getDishRecordId() {
        return dishRecordId;
    }

    public void setDishRecordId(Long dishRecordId) {
        this.dishRecordId = dishRecordId;
    }

    public LocalDateTime getDishRecordTime() {
        return dishRecordTime;
    }

    public void setDishRecordTime(LocalDateTime dishRecordTime) {
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

    public DishRecord() {
    }

    public DishRecord(Long dishRecordId, LocalDateTime dishRecordTime, String dishRecordDesc, Dish dish) {
        this.dishRecordId = dishRecordId;
        this.dishRecordTime = dishRecordTime;
        this.dishRecordDesc = dishRecordDesc;
        this.dish = dish;
    }
}
