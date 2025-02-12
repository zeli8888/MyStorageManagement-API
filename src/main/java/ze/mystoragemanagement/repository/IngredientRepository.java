package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.Ingredient;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:30
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
