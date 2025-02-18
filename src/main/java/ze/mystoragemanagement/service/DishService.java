package ze.mystoragemanagement.service;

import org.springframework.stereotype.Service;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.model.Dish;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:31
 * @Version : V1.0
 * @Description :
 */
public interface DishService {
    List<Dish> getAllDishes();
    Dish getDishById(Long dishId);
    Dish getDishByName(String dishName);
//    Dish createDish(Dish dish);
//    Dish updateDish(Long dishId, Dish dish);
    Dish createDish(DishIngredientDTO dishIngredientDTO);
    Dish updateDish(Long dishId, DishIngredientDTO dishIngredientDTO);
    void deleteDish(Long dishId);

}
