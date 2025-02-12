package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.service.DishService;

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

    @Override
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    public Dish updateDish(Long dishId, Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    public void deleteDish(Long dishId) {
        dishRepository.deleteById(dishId);
    }
}
