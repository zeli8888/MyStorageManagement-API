package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;
import ze.mystoragemanagement.service.DishRecordService;

import java.util.Collection;
import java.util.HashSet;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:44
 * @Version : V1.0
 * @Description :
 */
@Service
public class DishRecordServiceImpl implements DishRecordService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private DishRecordRepository dishRecordRepository;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private FirebaseSecurityContextId firebaseSecurityContextId;

    private String getCurrentUserFirebaseId() {
        return firebaseSecurityContextId.getCurrentFirebaseId();
    }

    @Override
    public Page<DishRecord> getAllDishRecords(Pageable pageable) {
        return dishRecordRepository.findAllByFirebaseId(getCurrentUserFirebaseId(), pageable);
    }

    @Override
    public DishRecord getDishRecordById(Long dishRecordId) {
        return dishRecordRepository.findByDishRecordIdAndFirebaseId(dishRecordId, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DishRecord with id " + dishRecordId + " not found"));
    }

    @Transactional
    @Override
    public DishRecord createDishRecord(DishRecordIngredientDTO dishRecordIngredientDTO) {
        dishRecordIngredientDTO.getDishRecord().setDishRecordId(null);
        return saveDishRecord(dishRecordIngredientDTO);
    }

    @Transactional
    @Override
    public DishRecord updateDishRecord(Long dishRecordId, DishRecordIngredientDTO dto) {
        dto.getDishRecord().setDishRecordId(dishRecordId);
        return saveDishRecord(dto);
    }

    private DishRecord saveDishRecord(DishRecordIngredientDTO dto) {
        String firebaseId = getCurrentUserFirebaseId();
        DishRecord record = dto.getDishRecord();
        record.setFirebaseId(firebaseId);
        Long recordId = record.getDishRecordId();

        // update record, revert stock
        if (recordId != null) {
            DishRecord oldRecord = dishRecordRepository.findByDishRecordIdAndFirebaseId(recordId, firebaseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DishRecord with id " + recordId + " not found"));
            revertIngredientStock(oldRecord);
        }

        // verify dish exists, set dish from database
        if (record.getDish() != null) {
            Dish dish = dishRepository.findByDishNameAndFirebaseId(record.getDish().getDishName(), firebaseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with name " + record.getDish().getDishName() + " not found"));
            record.setDish(dish);
        }

        processIngredients(dto, record, firebaseId);
        return dishRecordRepository.save(record);
    }

    private void revertIngredientStock(DishRecord oldRecord) {
        if (oldRecord.getDishRecordIngredients() == null) return;
        for (DishRecordIngredient ri : oldRecord.getDishRecordIngredients()) {
            Ingredient ingredient = ri.getIngredient();
            ingredient.setIngredientStorage(ingredient.getIngredientStorage() + ri.getDishRecordIngredientQuantity());
            ingredientRepository.save(ingredient);
        }
    }

    private void processIngredients(DishRecordIngredientDTO dto, DishRecord record, String firebaseId) {
        record.setDishRecordIngredients(new HashSet<>());

        if (dto.getIngredientIdQuantityList() == null) return;
        for (IngredientIdQuantityDTO item : dto.getIngredientIdQuantityList()) {
            Ingredient ingredient = item.getIngredientId() != null ?
                    ingredientRepository.findByIngredientIdAndFirebaseId(item.getIngredientId(), firebaseId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with id " + item.getIngredientId() + " not found")) :
                    ingredientRepository.findByIngredientNameAndFirebaseId(item.getIngredientName(), firebaseId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with name " + item.getIngredientName() + " not found"));

            // update storage
            ingredient.setIngredientStorage(ingredient.getIngredientStorage() - item.getQuantity());
            ingredientRepository.save(ingredient);

            // add link to record
            DishRecordIngredient ri = new DishRecordIngredient(
                    new DishRecordIngredientId(ingredient.getIngredientId(), record.getDishRecordId()),
                    item.getQuantity(),
                    record,
                    ingredient
            );
            record.getDishRecordIngredients().add(ri);
        }
    }

    @Override
    @Transactional
    public void deleteDishRecords(Collection<Long> dishRecordIds) {
        if (!dishRecordIds.isEmpty()) {
            dishRecordRepository.deleteAllByIdInAndFirebaseId(dishRecordIds, getCurrentUserFirebaseId());
        }
    }

    @Override
    public Page<DishRecord> searchDishRecords(String searchString, Pageable pageable) {
        return dishRecordRepository.searchDishRecordsByFirebaseId(searchString, getCurrentUserFirebaseId(), pageable);
    }
}
