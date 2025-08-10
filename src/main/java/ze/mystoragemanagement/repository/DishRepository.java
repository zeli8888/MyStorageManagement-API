package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.Dish;

import java.util.Collection;
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
    List<Dish> findAllByFirebaseId(String firebaseId);
    Optional<Dish> findByDishIdAndFirebaseId(Long dishId, String firebaseId);
    Optional<Dish> findByDishNameAndFirebaseId(String dishName, String firebaseId);

    @Query("SELECT DISTINCT d FROM Dish d " +
            "LEFT JOIN d.dishIngredients di " +
            "LEFT JOIN di.ingredient i " +
            "WHERE d.firebaseId = :firebaseId " +
            "AND (LOWER(d.dishName) LIKE LOWER(CONCAT('%', :searchString, '%')) " +
            "     OR LOWER(d.dishDesc) LIKE LOWER(CONCAT('%', :searchString, '%')) " +
            "     OR LOWER(i.ingredientName) LIKE LOWER(CONCAT('%', :searchString, '%')) " +
            "     OR LOWER(i.ingredientDesc) LIKE LOWER(CONCAT('%', :searchString, '%')))")
    List<Dish> searchDishesByFirebaseId(@Param("searchString") String searchString,
                                        @Param("firebaseId") String firebaseId);

}
