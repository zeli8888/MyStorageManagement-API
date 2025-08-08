package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.exception.IngredientNotFoundException;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.service.DishService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:31
 * @Version : V1.0
 * @Description :
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private DishRecordRepository dishRecordRepository;

    @Override
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    @Override
    public Dish getDishById(Long dishId) {
        return dishRepository.findById(dishId).orElse(null);
    }

    @Override
    public Dish getDishByName(String dishName) {
        return dishRepository.findDishByDishName(dishName).orElse(null);
    }


    @Transactional
    @Override
    public Dish createDish(DishIngredientDTO dishIngredientDTO) {
        return saveDish(dishIngredientDTO);
    }


    @Transactional
    @Override
    public Dish updateDish(Long dishId, DishIngredientDTO dishIngredientDTO) {
        dishIngredientDTO.getDish().setDishId(dishId);
        return saveDish(dishIngredientDTO);
    }


    private Dish saveDish(DishIngredientDTO dishIngredientDTO){
        Dish dish = dishIngredientDTO.getDish();
        dish.setDishIngredients(new HashSet<>());
        IngredientIdQuantityDTO[] ingredientIdQuantityList = dishIngredientDTO.getIngredientIdQuantityList();
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
                DishIngredient dishIngredient = new DishIngredient(new DishIngredientId(dish.getDishId(), ingredient.getIngredientId()), quantity, dish, ingredient);
                dish.getDishIngredients().add(dishIngredient);
            }
        }
        return dishRepository.save(dish);
    }

    @Override
    @Transactional
    public void deleteDishes(Collection<Long> dishIds) {
        for (Long dishId : dishIds) {
            Dish dish = dishRepository.findById(dishId).orElseThrow(()->new IllegalArgumentException("Dish with id "+dishId+" not found"));
            for (DishRecord dishRecord : dish.getDishRecords()) {
                dishRecord.setDish(null);
                dishRecordRepository.save(dishRecord);
            }
            dishRepository.deleteById(dishId);
        }
    }

    @Override
    public List<Dish> searchDishes(String searchString) {
        return dishRepository.searchDishes(searchString);
    }
}
