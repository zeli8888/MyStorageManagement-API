package ze.mystoragemanagement.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ze.mystoragemanagement.dto.DishRecordAnalysisDTO;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.DishRecordPage;
import ze.mystoragemanagement.model.DishRecord;
import ze.mystoragemanagement.model.Views;
import ze.mystoragemanagement.service.DishRecordService;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 21:32
 * @Version : V1.0
 * @Description :
 */

@Validated
@RestController
public class DishRecordController {
    @Autowired
    private DishRecordService dishRecordService;

    @GetMapping("/dishrecords")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecordPage> getAllDishRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dishRecordTime").descending());
        return ResponseEntity.ok(new DishRecordPage(dishRecordService.getAllDishRecords(pageable)));
    }

    @GetMapping("/dishrecords/search")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecordPage> searchDishRecords(
            @RequestParam String searchString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dishRecordTime").descending());
        return ResponseEntity.ok(new DishRecordPage(dishRecordService.searchDishRecords(searchString, pageable)));
    }

    @GetMapping("/dishrecords/{dishRecordId}")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecord> getDishRecordById(@PathVariable Long dishRecordId) {
        return ResponseEntity.ok(dishRecordService.getDishRecordById(dishRecordId));
    }

    @PostMapping("/dishrecords")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecord> createDishRecord(@RequestBody DishRecordIngredientDTO dishRecordIngredientDTO) {
        DishRecord createdDishRecord = dishRecordService.createDishRecord(dishRecordIngredientDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{dishRecordId}").buildAndExpand(createdDishRecord.getDishRecordId()).toUri();
        return ResponseEntity.created(uri).body(createdDishRecord);
    }

    @PutMapping("/dishrecords/{dishRecordId}")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecord> updateDishRecord(@PathVariable Long dishRecordId, @RequestBody DishRecordIngredientDTO dishRecordIngredientDTO) {
        return ResponseEntity.ok(dishRecordService.updateDishRecord(dishRecordId, dishRecordIngredientDTO));
    }

    @DeleteMapping("/dishrecords")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<Void> deleteDishRecords(@RequestBody List<Long> dishRecordIds) {
        dishRecordService.deleteDishRecords(dishRecordIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dishrecords/analysis")
    @JsonView(Views.DishRecordView.class)
    public ResponseEntity<DishRecordAnalysisDTO> getDishRecordAnalysis(@RequestParam @NotNull ZonedDateTime startTime, @RequestParam @NotNull ZonedDateTime endTime) {
        if (startTime.isAfter(endTime)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time.");
        return ResponseEntity.ok(dishRecordService.getDishRecordAnalysis(startTime, endTime));
    }
}
