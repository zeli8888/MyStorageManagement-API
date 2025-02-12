package ze.mystoragemanagement.service;

import ze.mystoragemanagement.model.DishRecord;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:43
 * @Version : V1.0
 * @Description :
 */

public interface DishRecordService {
    List<DishRecord> getAllDishRecords();
    DishRecord getDishRecordById(Long dishRecordId);
    DishRecord createDishRecord(DishRecord dishRecord);
    DishRecord updateDishRecord(Long dishRecordId, DishRecord dishRecord);
    void deleteDishRecord(Long dishRecordId);
}
