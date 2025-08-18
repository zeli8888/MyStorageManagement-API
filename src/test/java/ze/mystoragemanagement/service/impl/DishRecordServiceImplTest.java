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
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;

import java.time.ZonedDateTime;
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
                new IngredientIdQuantityDTO(1L, 5L),
                new IngredientIdQuantityDTO(2L, 10L)
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
        ingredient.setIngredientStorage(5L);
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
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(5L, "test ingredient");
        testDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientStorage(5L);
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
        existingIngredient.setIngredientStorage(10L);

        // Prepare existing record
        DishRecord existingRecord = new DishRecord();
        DishRecordIngredient ri = new DishRecordIngredient(
                new DishRecordIngredientId(1L, 1L), 3L, existingRecord, existingIngredient
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
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(1L, 5L);
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
        existingIngredient.setIngredientStorage(10L);

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

        doNothing().when(dishRecordRepository).deleteAllByIdInAndFirebaseId(anyCollection(), anyString());

        dishRecordService.deleteDishRecords(Collections.singletonList(1L));

        verify(dishRecordRepository).deleteAllByIdInAndFirebaseId(anyCollection(), captor.capture());
        assertEquals(TEST_FIREBASE_ID, captor.getValue());
    }

    @Test
    void deleteDishRecords_Null() {
        dishRecordService.deleteDishRecords(Collections.emptySet());
        verify(dishRecordRepository, times(0)).deleteAllByIdInAndFirebaseId(anyCollection(), anyString());
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
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(5L, "test ingredient");
        testDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);

        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishRecordService.createDishRecord(testDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
