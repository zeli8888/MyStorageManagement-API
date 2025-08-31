package ze.mystoragemanagement.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.model.Views;

/**
 * @Author : Ze Li
 * @Date : 30/08/2025 14:25
 * @Version : V1.0
 * @Description :
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSummaryDTO {
    @JsonView(Views.DishRecordView.class)
    private Ingredient ingredient;
    @JsonView(Views.DishRecordView.class)
    private Double totalCost;
    @JsonView(Views.DishRecordView.class)
    private Double totalUsage;
    @JsonView(Views.DishRecordView.class)
    private Double dailyUsage;
    @JsonView(Views.DishRecordView.class)
    private Double supplyDays;
}