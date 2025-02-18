package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ze.mystoragemanagement.model.DishIngredient;
import ze.mystoragemanagement.model.DishIngredientId;
import ze.mystoragemanagement.model.DishRecord;

/**
 * @Author : Ze Li
 * @Date : 18/02/2025 00:05
 * @Version : V1.0
 * @Description :
 */
public interface DishIngredientRepository extends JpaRepository<DishIngredient, DishIngredientId> {
}
