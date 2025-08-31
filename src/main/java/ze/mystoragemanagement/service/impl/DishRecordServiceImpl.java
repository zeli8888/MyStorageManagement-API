package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.dto.*;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;
import ze.mystoragemanagement.service.DishRecordService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.hibernate.engine.spi.EntityKey;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
/**
 * @Author : Ze Li
 * @Date : 12/02/2025 15:44
 * @Version : V1.0
 * @Description :
 */
@Service
public class DishRecordServiceImpl implements DishRecordService {
//    @PersistenceContext
//    private EntityManager entityManager;
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
            // NO NEED TO DETACH
//            entityManager.detach(oldRecord);
        }

        // verify dish exists, set dish from database
        if (record.getDish() != null) {
            Dish dish = dishRepository.findByDishNameAndFirebaseId(record.getDish().getDishName(), firebaseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish with name " + record.getDish().getDishName() + " not found"));
            record.setDish(dish);
        }

        // DEBUG ONLY
//        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
//        org.hibernate.engine.spi.PersistenceContext context = session.getPersistenceContext();
//        Map<EntityKey,Object> entities = context.getEntitiesByKey();

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
                    new DishRecordIngredientId(record.getDishRecordId(), ingredient.getIngredientId()),
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
            List<DishRecord> records = dishRecordRepository.findAllByIdInAndFirebaseId(dishRecordIds, getCurrentUserFirebaseId());
            dishRecordRepository.deleteAll(records);
        }
    }

    @Override
    public Page<DishRecord> searchDishRecords(String searchString, Pageable pageable) {
        return dishRecordRepository.searchDishRecordsByFirebaseId(searchString, getCurrentUserFirebaseId(), pageable);
    }

    @Override
    public DishRecordAnalysisDTO getDishRecordAnalysis(ZonedDateTime startTime, ZonedDateTime endTime) {
        List<DishRecord> records = dishRecordRepository.findAllByDishRecordTimeBetweenAndFirebaseId(startTime, endTime, getCurrentUserFirebaseId());

        DishRecordAnalysisDTO analysis = new DishRecordAnalysisDTO();
        analysis.setStartTime(startTime);
        analysis.setEndTime(endTime);
        long days = ChronoUnit.DAYS.between(startTime, endTime) + 1;

        HashMap<Long, IngredientSummaryDTO> ingredientSummaryMap = new HashMap<>();
        HashMap<Long, DishSummaryDTO> dishSummaryMap = new HashMap<>();

        for (DishRecord record : records) {
            for (DishRecordIngredient ri : record.getDishRecordIngredients()) {
                Ingredient ingredient = ri.getIngredient();
                IngredientSummaryDTO ingredientSummaryDTO = ingredientSummaryMap.getOrDefault(
                        ingredient.getIngredientId(), new IngredientSummaryDTO(ingredient, 0.0, 0.0, 0.0, 0.0)
                );
                if (ingredient.getIngredientCost() != null){
                    ingredientSummaryDTO.setTotalCost(ingredientSummaryDTO.getTotalCost() + ri.getDishRecordIngredientQuantity() * ingredient.getIngredientCost());
                }
                ingredientSummaryDTO.setTotalUsage(ingredientSummaryDTO.getTotalUsage() + ri.getDishRecordIngredientQuantity());
                ingredientSummaryMap.put(ingredient.getIngredientId(), ingredientSummaryDTO);
            }

            Dish dish = record.getDish();
            if (dish == null) continue;
            DishSummaryDTO dishSummaryDTO = dishSummaryMap.getOrDefault(
                    dish.getDishId(), new DishSummaryDTO(dish, 0)
            );
            dishSummaryDTO.setTotalUsage(dishSummaryDTO.getTotalUsage() + 1);
            dishSummaryMap.put(dish.getDishId(), dishSummaryDTO);
        }

        ArrayList<IngredientSummaryDTO> ingredientsSummary = new ArrayList<>(ingredientSummaryMap.values());
        ingredientsSummary.forEach(ingredientSummaryDTO -> {
            ingredientSummaryDTO.setDailyUsage(
                    BigDecimal.valueOf(ingredientSummaryDTO.getTotalUsage()).divide(
                            BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP
                    ).doubleValue()
            );

            Double storage = ingredientSummaryDTO.getIngredient().getIngredientStorage();
            if (storage != null) {
                Double dailyUsage = ingredientSummaryDTO.getDailyUsage();
                double supplyDays = 0;
                if (dailyUsage == 0) supplyDays = 365;
                else if (storage < 0) supplyDays = storage * dailyUsage;
                else supplyDays = storage / dailyUsage;
                ingredientSummaryDTO.setSupplyDays(BigDecimal.valueOf(supplyDays).setScale(2, RoundingMode.HALF_UP).doubleValue());
            };

            ingredientSummaryDTO.setTotalCost(
                    BigDecimal.valueOf(ingredientSummaryDTO.getTotalCost()).setScale(2, RoundingMode.HALF_UP).doubleValue()
            );
        });

        analysis.setIngredientsSummary(ingredientsSummary);
        analysis.setDishesSummary(new ArrayList<>(dishSummaryMap.values()));
        return analysis;
    }
}
