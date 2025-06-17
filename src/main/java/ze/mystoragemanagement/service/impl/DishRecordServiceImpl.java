package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.exception.DishNotFoundException;
import ze.mystoragemanagement.exception.IngredientNotFoundException;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
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
    private IngredientRepository ingredientRepository;
    @Autowired
    private DishRecordRepository dishRecordRepository;
    @Autowired
    private DishRepository dishRepository;

    @Override
    public List<DishRecord> getAllDishRecords() {
        return dishRecordRepository.findAllByOrderByDishRecordTimeDesc();
    }

    @Override
    public DishRecord getDishRecordById(Long dishRecordId) {
        return dishRecordRepository.findById(dishRecordId).orElse(null);
    }

    @Transactional
    @Override
    public DishRecord createDishRecord(DishRecordIngredientDTO dishRecordIngredientDTO) {
        return saveDishRecord(dishRecordIngredientDTO);
    }

    private DishRecord saveDishRecord(DishRecordIngredientDTO dishRecordIngredientDTO){
        DishRecord dishRecord = dishRecordIngredientDTO.getDishRecord();
        Dish dish = dishRecord.getDish();
        if (dish != null) dishRecord.setDish(dishRepository.findDishByDishName(dish.getDishName()).orElseThrow(()->new DishNotFoundException("name "+dish.getDishName())));
        dishRecord.setDishRecordIngredients(new HashSet<>());
        IngredientIdQuantityDTO[] ingredientIdQuantityList = dishRecordIngredientDTO.getIngredientIdQuantityList();
        if (ingredientIdQuantityList != null) {
            for (IngredientIdQuantityDTO ingredientIdQuantity : ingredientIdQuantityList){
                Long ingredientId = ingredientIdQuantity.getIngredientId();
                Ingredient ingredient;
                if (ingredientId != null) {
                    ingredient = ingredientRepository.findById(ingredientId).orElseThrow(()->new IngredientNotFoundException("id "+ingredientId));
                }else{
                    ingredient = ingredientRepository.findIngredientByIngredientName(ingredientIdQuantity.getIngredientName()).orElseThrow(()->new IngredientNotFoundException("name "+ingredientIdQuantity.getIngredientName()));
                }
                long quantity = ingredientIdQuantity.getQuantity();
                DishRecordIngredient dishRecordIngredient = new DishRecordIngredient(new DishRecordIngredientId(ingredient.getIngredientId(), dishRecord.getDishRecordId()), quantity, dishRecord, ingredient);
                dishRecord.getDishRecordIngredients().add(dishRecordIngredient);
            }
        }
        return dishRecordRepository.save(dishRecord);
    }

    @Transactional
    @Override
    public DishRecord updateDishRecord(Long dishRecordId, DishRecordIngredientDTO dishRecordIngredientDTO) {
        dishRecordIngredientDTO.getDishRecord().setDishRecordId(dishRecordId);
        return saveDishRecord(dishRecordIngredientDTO);
    }

    @Override
    public void deleteDishRecord(Long dishRecordId) {
        dishRecordRepository.deleteById(dishRecordId);
    }

    @Override
    public List<DishRecord> searchDishRecords(String searchString){
        return dishRecordRepository.searchDishRecords(searchString);
    }

}
