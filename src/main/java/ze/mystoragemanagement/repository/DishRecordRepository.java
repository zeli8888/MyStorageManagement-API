package ze.mystoragemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.DishRecord;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:29
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface DishRecordRepository extends JpaRepository<DishRecord, Long> {
    Page<DishRecord> findAllByFirebaseId(String firebaseId, Pageable pageable);
    Optional<DishRecord> findByDishRecordIdAndFirebaseId(Long dishRecordId, String firebaseId);

    @Query("SELECT DISTINCT dr FROM DishRecord dr WHERE dr.dishRecordId IN :ids AND dr.firebaseId = :firebaseId")
    List<DishRecord> findAllByIdInAndFirebaseId(@Param("ids") Collection<Long> ids,
                                      @Param("firebaseId") String firebaseId);

    @Query("SELECT DISTINCT dr FROM DishRecord dr " +
            "LEFT JOIN dr.dish d " +
            "LEFT JOIN dr.dishRecordIngredients dri " +
            "LEFT JOIN dri.ingredient i " +
            "WHERE dr.firebaseId = :firebaseId " +
            "AND (LOWER(dr.dishRecordDesc) LIKE LOWER(CONCAT('%', :searchString, '%'))" +
            "OR LOWER(d.dishName) LIKE LOWER(CONCAT('%', :searchString, '%'))" +
            "OR LOWER(d.dishDesc) LIKE LOWER(CONCAT('%', :searchString, '%'))" +
            "OR LOWER(i.ingredientName) LIKE LOWER(CONCAT('%', :searchString, '%'))" +
            "or LOWER(i.ingredientDesc) LIKE LOWER(CONCAT('%', :searchString, '%'))) ")
    Page<DishRecord> searchDishRecordsByFirebaseId(@Param("searchString") String searchString,
                                                   @Param("firebaseId") String firebaseId,
                                                   Pageable pageable);
}
