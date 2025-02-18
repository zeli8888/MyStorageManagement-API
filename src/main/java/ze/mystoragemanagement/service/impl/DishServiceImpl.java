package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.exception.IngredientNotFoundException;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.model.DishIngredient;
import ze.mystoragemanagement.model.DishIngredientId;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.service.DishService;

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

//    @Override
//    public Dish createDish(Dish dish) {
//        return saveDish(dish);
//    }

    @Transactional
    @Override
    public Dish createDish(DishIngredientDTO dishIngredientDTO) {
        return saveDish(dishIngredientDTO);
    }

//    @Override
//    public Dish updateDish(Long dishId, Dish dish) {
//        dish.setDishId(dishId);
//        return saveDish(dish);
//    }

    @Transactional
    @Override
    public Dish updateDish(Long dishId, DishIngredientDTO dishIngredientDTO) {
        dishIngredientDTO.getDish().setDishId(dishId);
        return saveDish(dishIngredientDTO);
    }

//    private Dish saveDish(Dish dish){
//        Set<Ingredient> ingredients = dish.getDishIngredients();
//        if (ingredients != null) {
//            HashSet<Ingredient> ingredientsStored = new HashSet<>();
//            for (Ingredient ingredient : ingredients) {
//                Ingredient ingredientStored = ingredientRepository.findById(ingredient.getIngredientId()).orElseThrow(()->new IngredientNotFoundException(ingredient.getIngredientName()));
//                ingredientsStored.add(ingredientStored);
//            }
//            dish.setDishIngredients(ingredientsStored);
//        }
//        return dishRepository.save(dish);
//    }

    private Dish saveDish(DishIngredientDTO dishIngredientDTO){
        Dish dish = dishIngredientDTO.getDish();
        dish.setDishIngredients(new HashSet<>());
        IngredientIdQuantityDTO[] ingredientIdQuantityList = dishIngredientDTO.getIngredientIdQuantityList();
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
    public void deleteDish(Long dishId) {
        dishRepository.deleteById(dishId);
    }
}
