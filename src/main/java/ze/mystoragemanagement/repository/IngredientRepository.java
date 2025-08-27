package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.Ingredient;

import java.util.Collection;
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
    List<Ingredient> findAllByFirebaseId(String firebaseId);
    Optional<Ingredient> findByIngredientIdAndFirebaseId(Long id, String firebaseId);
    Optional<Ingredient> findByIngredientNameAndFirebaseId(String name, String firebaseId);

    @Query("SELECT DISTINCT i FROM Ingredient i WHERE i.ingredientId IN :ids AND i.firebaseId = :firebaseId")
    List<Ingredient> findAllByIdInAndFirebaseId(@Param("ids") Collection<Long> ids,
                                      @Param("firebaseId") String firebaseId);

    @Query("SELECT DISTINCT i FROM Ingredient i " +
            "WHERE i.firebaseId = :firebaseId " +
            "AND (LOWER(i.ingredientName) LIKE LOWER(CONCAT('%', :searchString, '%')) " +
            "OR LOWER(i.ingredientDesc) LIKE LOWER(CONCAT('%', :searchString, '%'))) ")
    List<Ingredient> searchIngredientsByFirebaseId(@Param("searchString") String searchString,
                                                   @Param("firebaseId") String firebaseId);
}
