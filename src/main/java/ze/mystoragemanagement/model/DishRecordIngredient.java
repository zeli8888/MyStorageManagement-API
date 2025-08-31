package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author : Ze Li
 * @Date : 15/04/2025 12:10
 * @Version : V1.0
 * @Description :
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish_record_ingredient")
public class DishRecordIngredient {
    @EmbeddedId
    @JsonIgnore
    private DishRecordIngredientId dishRecordIngredientId;
    @JsonView(Views.DishRecordView.class)
    @NotNull
    private Double dishRecordIngredientQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishRecordId")
    @JoinColumn(name = "dish_record_id")
    @JsonIgnore
    private DishRecord dishRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    @JsonView(Views.DishRecordView.class)
    private Ingredient ingredient;
}
