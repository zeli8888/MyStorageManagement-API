package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.Dish;

import java.util.List;
import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:28
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findDishByDishName(String dishName);

    @Query("SELECT d FROM Dish d " +
        "JOIN d.dishIngredients di " +
        "JOIN di.ingredient i " +
        "WHERE i.ingredientName LIKE %?1% OR i.ingredientDesc LIKE %?1% OR d.dishName LIKE %?1% OR d.dishDesc LIKE %?1% ")
    List<Dish> searchDishes(String searchString);
}
