package ze.mystoragemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.model.DishRecord;
import ze.mystoragemanagement.service.DishRecordService;

import java.net.URI;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 21:32
 * @Version : V1.0
 * @Description :
 */

@RestController
public class DishRecordController {
    @Autowired
    private DishRecordService dishRecordService;

    @GetMapping("/dishrecords")
    public ResponseEntity<List<DishRecord>> getAllDishRecords() {
        return ResponseEntity.ok(dishRecordService.getAllDishRecords());
    }

    @GetMapping("/dishrecords/{dishRecordId}")
    public ResponseEntity<DishRecord> getDishRecordById(@PathVariable Long dishRecordId) {
        return ResponseEntity.ok(dishRecordService.getDishRecordById(dishRecordId));
    }

    @PostMapping("/dishrecords")
    public ResponseEntity<DishRecord> createDishRecord(@RequestBody DishRecordIngredientDTO dishRecordIngredientDTO) {
        DishRecord createdDishRecord = dishRecordService.createDishRecord(dishRecordIngredientDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{dishRecordId}").buildAndExpand(createdDishRecord.getDishRecordId()).toUri();
        return ResponseEntity.created(uri).body(createdDishRecord);
    }

    @PutMapping("/dishrecords/{dishRecordId}")
    public ResponseEntity<DishRecord> updateDishRecord(@PathVariable Long dishRecordId, @RequestBody DishRecordIngredientDTO dishRecordIngredientDTO) {
        return ResponseEntity.ok(dishRecordService.updateDishRecord(dishRecordId, dishRecordIngredientDTO));
    }

    @DeleteMapping("/dishrecords/{dishRecordId}")
    public ResponseEntity<Void> deleteDishRecord(@PathVariable Long dishRecordId) {
        dishRecordService.deleteDishRecord(dishRecordId);
        return ResponseEntity.noContent().build();
    }

}
