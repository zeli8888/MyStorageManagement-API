package ze.mystoragemanagement.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ze.mystoragemanagement.model.Dish;
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
public class DishSummaryDTO {
    @JsonView(Views.DishRecordView.class)
    private Dish dish;
    @JsonView(Views.DishRecordView.class)
    private Integer totalUsage;
}
