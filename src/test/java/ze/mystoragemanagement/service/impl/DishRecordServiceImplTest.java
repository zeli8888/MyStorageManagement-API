package ze.mystoragemanagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.dto.*;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DishRecordServiceImplTest {
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private DishRecordRepository dishRecordRepository;
    @Mock
    private DishRepository dishRepository;
    @Mock
    private FirebaseSecurityContextId firebaseSecurityContextId;

    @InjectMocks
    private DishRecordServiceImpl dishRecordService;

    private final String TEST_FIREBASE_ID = "test-firebase-123";
    private Dish testDish;
    private DishRecord testRecord;
    private DishRecordIngredientDTO testDTO;

    @BeforeEach
    void setUp() {
        // Initialize test dish
        testDish = new Dish();
        testDish.setDishId(1L);
        testDish.setDishName("Test Dish");
        testDish.setFirebaseId(TEST_FIREBASE_ID);

        // Initialize test record
        testRecord = new DishRecord();
        testRecord.setDishRecordId(1L);
        testRecord.setDishRecordTime(ZonedDateTime.now());
        testRecord.setDishRecordDesc("Test Record");
        testRecord.setFirebaseId(TEST_FIREBASE_ID);
        testRecord.setDish(testDish);

        // Initialize test DTO
        List<IngredientIdQuantityDTO> ingredients = Arrays.asList(
                new IngredientIdQuantityDTO(1L, 5.0),
                new IngredientIdQuantityDTO(2L, 10.0)
        );
        testDTO = new DishRecordIngredientDTO(testRecord, ingredients.toArray(new IngredientIdQuantityDTO[0]));
    }

    // Basic CRUD Tests
    @Test
    void getAllDishRecords_Success() {
        Pageable pageable = PageRequest.of(0, 25);
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findAllByFirebaseId(TEST_FIREBASE_ID, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(testRecord)));

        Page<DishRecord> result = dishRecordService.getAllDishRecords(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Test Record", result.getContent().get(0).getDishRecordDesc());
    }

    @Test
    void getDishRecordById_Found() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findByDishRecordIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testRecord));

        DishRecord result = dishRecordService.getDishRecordById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getDishRecordId());
    }

    @Test
    void getDishRecordById_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findByDishRecordIdAndFirebaseId(99L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.getDishRecordById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // Record Creation Tests
    @Test
    void createDishRecord_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientStorage(5.0);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.of(ingredient));
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(testDish));
        when(dishRecordRepository.save(any())).thenReturn(testRecord);

        DishRecord result = dishRecordService.createDishRecord(testDTO);

        assertNotNull(result);
        verify(ingredientRepository, times(2)).save(any());
    }

    @Test
    void createDishRecord_IngredientName() {
        IngredientIdQuantityDTO[] ingredientIdQuantityDTOS = new IngredientIdQuantityDTO[1];
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(5.0, "test ingredient");
        testDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientStorage(5.0);
        when(ingredientRepository.findByIngredientNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(ingredient));
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(testDish));
        when(dishRecordRepository.save(any())).thenReturn(testRecord);

        DishRecord result = dishRecordService.createDishRecord(testDTO);

        assertNotNull(result);
        verify(ingredientRepository, times(1)).save(any());
    }

    @Test
    void createDishRecord_IngredientNotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(testDish));
        when(ingredientRepository.findByIngredientIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.createDishRecord(testDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createDishRecord_DishNotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.createDishRecord(testDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateDishRecord_StockRevertVerification() {
        // Prepare existing ingredient with quantity
        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setIngredientStorage(10.0);

        // Prepare existing record
        DishRecord existingRecord = new DishRecord();
        DishRecordIngredient ri = new DishRecordIngredient(
                new DishRecordIngredientId(1L, 1L), 3.0, existingRecord, existingIngredient
        );
        existingRecord.setDishRecordIngredients(new HashSet<>(Collections.singletonList(ri)));

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findByDishRecordIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.of(existingRecord));
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(testDish));
        when(ingredientRepository.findByIngredientIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.of(existingIngredient));
        ArgumentCaptor<Ingredient> captor = ArgumentCaptor.forClass(Ingredient.class);

        IngredientIdQuantityDTO[] ingredientIdQuantityDTOS = new IngredientIdQuantityDTO[1];
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(1L, 5.0);
        dishRecordService.updateDishRecord(1L, new DishRecordIngredientDTO(testRecord, ingredientIdQuantityDTOS));

        // revert and update
        verify(ingredientRepository, times(2)).save(captor.capture());
        // original 10 storage, consume 3 on record, change record to 5 --> 8 as new storage
        assertEquals(8L, captor.getAllValues().get(1).getIngredientStorage()); // Verify stock revert
    }

    @Test
    void updateDishRecord_NoRevert() {
        // Prepare existing ingredient with quantity
        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setIngredientStorage(10.0);

        // Prepare existing record
        DishRecord existingRecord = new DishRecord();
        existingRecord.setDishRecordIngredients(null);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findByDishRecordIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.of(existingRecord));
        when(dishRepository.findByDishNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.of(testDish));

        dishRecordService.updateDishRecord(1L, new DishRecordIngredientDTO(testRecord, null));

        verify(ingredientRepository, times(0)).save(any());
    }

    @Test
    void updateDishRecord_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findByDishRecordIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.updateDishRecord(1L, new DishRecordIngredientDTO(testRecord, null)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // Deletion Tests
    @Test
    void deleteDishRecords_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(dishRecordRepository.findAllByIdInAndFirebaseId(anyCollection(), anyString())).thenReturn(Collections.singletonList(testRecord));
        doNothing().when(dishRecordRepository).deleteAll(anyCollection());
        dishRecordService.deleteDishRecords(Collections.singletonList(1L));

        verify(dishRecordRepository).findAllByIdInAndFirebaseId(anyCollection(), captor.capture());
        assertEquals(TEST_FIREBASE_ID, captor.getValue());
    }

    @Test
    void deleteDishRecords_Null() {
        dishRecordService.deleteDishRecords(Collections.emptySet());
        verify(dishRecordRepository, times(0)).findAllByIdInAndFirebaseId(anyCollection(), anyString());
    }

    // Search Tests
    @Test
    void searchDishRecords_Success() {
        Pageable pageable = PageRequest.of(0, 25);
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.searchDishRecordsByFirebaseId(anyString(), anyString(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(testRecord)));

        Page<DishRecord> result = dishRecordService.searchDishRecords("test", pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void saveDishRecord_IngredientIdNotFound() {
        testRecord.setDish(null);  // Clear dish reference
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.createDishRecord(testDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void saveDishRecord_IngredientNameNotFound() {
        testRecord.setDish(null);  // Clear dish reference
        IngredientIdQuantityDTO[] ingredientIdQuantityDTOS = new IngredientIdQuantityDTO[1];
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(5.0, "test ingredient");
        testDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.createDishRecord(testDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getDishRecordAnalysis() {
        ZonedDateTime start = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2024-01-03T23:59:59Z");
        int expectedDays = 3;

        testRecord.setDishRecordTime(ZonedDateTime.parse("2024-01-02T08:00:00Z"));
        DishRecord testRecord2 = new DishRecord();
        testRecord2.setDishRecordTime(ZonedDateTime.parse("2024-01-02T00:00:00Z"));
        testRecord2.setDishRecordId(2L);
        testRecord2.setDishRecordDesc("Test Record 2");
        testRecord2.setFirebaseId(TEST_FIREBASE_ID);

        // normal ingredient with full info
        Ingredient ingredient1 = new Ingredient(1L, "Egg",
                1.0, 0.5, "test egg", null, null,
                TEST_FIREBASE_ID);
        DishRecordIngredient dishRecordIngredient1 = new DishRecordIngredient(new DishRecordIngredientId(2L, 1L), 0.5, testRecord2, ingredient1);

        // record with 0 quantity consumed
        Ingredient ingredient2 = new Ingredient(2L, "Meat",
                1.0, 1.0, "test meat", null, null,
                TEST_FIREBASE_ID);
        DishRecordIngredient dishRecordIngredient2 = new DishRecordIngredient(new DishRecordIngredientId(2L, 2L), 0.0, testRecord2, ingredient2);

        // ingredient with null storage
        Ingredient ingredient3 = new Ingredient(3L, "Bread",
                null, 1.0, "test bread", null, null,
                TEST_FIREBASE_ID);
        DishRecordIngredient dishRecordIngredient3 = new DishRecordIngredient(new DishRecordIngredientId(2L, 3L), 0.5, testRecord2, ingredient3);

        // ingredient with null cost
        Ingredient ingredient4 = new Ingredient(4L, "Milk",
                1.0, null, "test milk", null, null,
                TEST_FIREBASE_ID);
        DishRecordIngredient dishRecordIngredient4 = new DishRecordIngredient(new DishRecordIngredientId(2L, 4L), 0.5, testRecord2, ingredient4);

        testRecord2.setDishRecordIngredients(Set.of(dishRecordIngredient1, dishRecordIngredient2, dishRecordIngredient3, dishRecordIngredient4));

        List<DishRecord> records = new ArrayList<>();
        records.add(testRecord);
        records.add(testRecord2);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRecordRepository.findAllByDishRecordTimeBetweenAndFirebaseId(any(), any(), eq(TEST_FIREBASE_ID))).thenReturn(records);
        DishRecordAnalysisDTO result = dishRecordService.getDishRecordAnalysis(start, end);

        assertEquals(expectedDays, ChronoUnit.DAYS.between(start, end) + 1);
        List<IngredientSummaryDTO> ingredients = result.getIngredientsSummary();
        assertEquals(4, ingredients.size());

        List<DishSummaryDTO> dishes = result.getDishesSummary();
        assertEquals(1, dishes.size());
    }
}
