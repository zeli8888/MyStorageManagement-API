package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;
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
    @Autowired
    private FirebaseSecurityContextId firebaseSecurityContextId;

    private String getCurrentUserFirebaseId() {
        return firebaseSecurityContextId.getCurrentFirebaseId();
    }

    @Override
    public List<Dish> getAllDishes() {
        return dishRepository.findAllByFirebaseId(getCurrentUserFirebaseId());
    }

    @Override
    public Dish getDishById(Long dishId) {
        return dishRepository.findByDishIdAndFirebaseId(dishId, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with id " + dishId + " not found"));
    }

    @Override
    public Dish getDishByName(String dishName) {
        return dishRepository.findByDishNameAndFirebaseId(dishName, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with name " + dishName + " not found"));
    }


    @Transactional
    @Override
    public Dish createDish(DishIngredientDTO dishIngredientDTO) {
        dishIngredientDTO.getDish().setDishId(null);
        return saveDish(dishIngredientDTO);
    }

    @Transactional
    @Override
    public Dish updateDish(Long dishId, DishIngredientDTO dishIngredientDTO) {
        dishRepository.findByDishIdAndFirebaseId(dishId, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with id " + dishId + " not found"));
        dishIngredientDTO.getDish().setDishId(dishId);
        return saveDish(dishIngredientDTO);
    }

    private Dish saveDish(DishIngredientDTO dishIngredientDTO) {
        String firebaseId = getCurrentUserFirebaseId();
        Dish dish = dishIngredientDTO.getDish();
        dish.setDishIngredients(new HashSet<>());
        dish.setFirebaseId(firebaseId);

        if (dishIngredientDTO.getIngredientIdQuantityList() != null) {
            for (IngredientIdQuantityDTO dto : dishIngredientDTO.getIngredientIdQuantityList()) {
                Ingredient ingredient = dto.getIngredientId() != null ?
                        ingredientRepository.findByIngredientIdAndFirebaseId(dto.getIngredientId(), firebaseId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with id " + dto.getIngredientId() + " not found")) :
                        ingredientRepository.findByIngredientNameAndFirebaseId(dto.getIngredientName(), firebaseId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with name " + dto.getIngredientName() + " not found"));

                DishIngredient dishIngredient = new DishIngredient(
                        new DishIngredientId(dish.getDishId(), ingredient.getIngredientId()),
                        dto.getQuantity(),
                        dish,
                        ingredient
                );
                dish.getDishIngredients().add(dishIngredient);
            }
        }
        return dishRepository.save(dish);
    }

    @Transactional
    @Override
    public void deleteDishes(Collection<Long> dishIds) {
        String firebaseId = getCurrentUserFirebaseId();
        for (Long dishId : dishIds) {
            Dish dish = dishRepository.findByDishIdAndFirebaseId(dishId, firebaseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with id " + dishId + " not found"));
            for (DishRecord dishRecord : dish.getDishRecords()) {
                dishRecord.setDish(null);
                dishRecordRepository.save(dishRecord);
            }
            dishRepository.deleteById(dishId);
        }
    }

    @Override
    public List<Dish> searchDishes(String searchString) {
        return dishRepository.searchDishesByFirebaseId(searchString, getCurrentUserFirebaseId());
    }
}
