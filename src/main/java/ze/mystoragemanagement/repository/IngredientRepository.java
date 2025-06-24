package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.model.Ingredient;

import java.util.List;
import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:30
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findIngredientByIngredientName(String ingredientName);

    @Query("SELECT DISTINCT i FROM Ingredient i " +
    "WHERE i.ingredientName LIKE %?1% OR i.ingredientDesc LIKE %?1% ")
    List<Ingredient> searchIngredients(String searchString);
}
