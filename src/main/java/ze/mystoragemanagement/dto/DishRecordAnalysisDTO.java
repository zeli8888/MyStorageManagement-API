package ze.mystoragemanagement.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ze.mystoragemanagement.model.Views;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 30/08/2025 13:57
 * @Version : V1.0
 * @Description :
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DishRecordAnalysisDTO {
    @JsonView(Views.DishRecordView.class)
    private ZonedDateTime startTime;
    @JsonView(Views.DishRecordView.class)
    private ZonedDateTime endTime;
    @JsonView(Views.DishRecordView.class)
    private List<IngredientSummaryDTO> ingredientsSummary;
    @JsonView(Views.DishRecordView.class)
    private List<DishSummaryDTO> dishesSummary;
}