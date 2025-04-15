package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.exception.IngredientNotFoundException;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.service.DishRecordService;

import java.util.HashSet;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:44
 * @Version : V1.0
 * @Description :
 */
@Service
public class DishRecordServiceImpl implements DishRecordService {
    @Autowired
    private DishRecordRepository DishRecordRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<DishRecord> getAllDishRecords() {
        return DishRecordRepository.findAll();
    }

    @Override
    public DishRecord getDishRecordById(Long dishRecordId) {
        return DishRecordRepository.findById(dishRecordId).orElse(null);
    }

    @Override
    public DishRecord createDishRecord(DishRecord dishRecord) {
        return DishRecordRepository.save(dishRecord);
    }

    private DishRecord saveDishRecord(DishRecordIngredientDTO dishRecordIngredientDTO){
        DishRecord dishRecord = dishRecordIngredientDTO.getDishRecord();
        dishRecord.setDishRecordIngredients(new HashSet<>());
        IngredientIdQuantityDTO[] ingredientIdQuantityList = dishRecordIngredientDTO.getIngredientIdQuantityList();
        if (ingredientIdQuantityList != null) {
            for (IngredientIdQuantityDTO ingredientIdQuantity : ingredientIdQuantityList){
                long ingredientId = ingredientIdQuantity.getIngredientId();
                Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(()->new IngredientNotFoundException("id "+ingredientId));
                long quantity = ingredientIdQuantity.getQuantity();
                DishIngredient dishIngredient = new DishIngredient(new DishIngredientId(dish.getDishId(), ingredientId), quantity, dish, ingredient);
                dish.getDishIngredients().add(dishIngredient);
            }
        }
        return dishRepository.save(dish);
    }
    @Override
    public DishRecord updateDishRecord(Long dishRecordId, DishRecord dishRecord) {
        dishRecord.setDishRecordId(dishRecordId);
        return DishRecordRepository.save(dishRecord);
    }

    @Override
    public void deleteDishRecord(Long dishRecordId) {
        DishRecordRepository.deleteById(dishRecordId);
    }
}
