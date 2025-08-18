package ze.mystoragemanagement.dto;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import ze.mystoragemanagement.model.DishRecord;
import ze.mystoragemanagement.model.Views;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 24/06/2025 00:31
 * @Version : V1.0
 * @Description :
 */


@Setter
@Getter
public class DishRecordPage {
    @JsonView(Views.DishRecordView.class)
    private List<DishRecord> content;

    @JsonView(Views.DishRecordView.class)
    private int page;

    @JsonView(Views.DishRecordView.class)
    private int size;

    @JsonView(Views.DishRecordView.class)
    private int totalPages;

    @JsonView(Views.DishRecordView.class)
    private long totalElements;

    public DishRecordPage(Page<DishRecord> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }

}

