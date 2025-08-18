package ze.mystoragemanagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRecordRepository;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DishServiceImplTest {

    @Mock private DishRepository dishRepository;
    @Mock private IngredientRepository ingredientRepository;
    @Mock private DishRecordRepository dishRecordRepository;
    @Mock private FirebaseSecurityContextId firebaseSecurityContextId;

    @InjectMocks private DishServiceImpl dishService;

    private final String TEST_FIREBASE_ID = "test-firebase-123";
    private Dish testDish;
    private DishIngredientDTO dishIngredientDTO;

    @BeforeEach
    void setUp() {
        // Initialize test dish
        testDish = new Dish();
        testDish.setDishId(1L);
        testDish.setDishName("Test Dish");
        testDish.setFirebaseId(TEST_FIREBASE_ID);

        // Initialize DTO with valid data
        List<IngredientIdQuantityDTO> ingredients = Arrays.asList(
                new IngredientIdQuantityDTO(1L, 2L),
                new IngredientIdQuantityDTO(2L, 3L)
        );
        dishIngredientDTO = new DishIngredientDTO(testDish, ingredients.toArray(new IngredientIdQuantityDTO[0]));
    }

    @Test
    void getAllDishes() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findAllByFirebaseId(TEST_FIREBASE_ID))
                .thenReturn(Collections.singletonList(testDish));

        List<Dish> result = dishService.getAllDishes();

        assertEquals(1, result.size());
        assertEquals("Test Dish", result.get(0).getDishName());
    }

    @Test
    void getDishById_Found() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testDish));

        Dish result = dishService.getDishById(1L);

        assertNotNull(result);
        assertEquals("Test Dish", result.getDishName());
    }

    @Test
    void getDishById_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(99L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.getDishById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Dish with id 99 not found"));
    }

    @Test
    void getDishByName_Found() {
        // Mock dependency
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishNameAndFirebaseId("Test Dish", TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testDish));

        // Execute test
        Dish result = dishService.getDishByName("Test Dish");

        // Verify result
        assertNotNull(result);
        assertEquals("Test Dish", result.getDishName());
    }

    @Test
    void getDishByName_NotFound() {
        // Mock dependency
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishNameAndFirebaseId("Unknown Dish", TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        // Verify exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.getDishByName("Unknown Dish"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Dish with name Unknown Dish not found"));
    }


    @Test
    void createDish_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(anyLong(), anyString()))
                .thenReturn(Optional.of(new Ingredient()));
        when(dishRepository.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        Dish result = dishService.createDish(dishIngredientDTO);

        assertEquals(2, result.getDishIngredients().size());
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    void createDish_Success_WithoutIngredientId() {
        IngredientIdQuantityDTO[] ingredientIdQuantityDTOS = new IngredientIdQuantityDTO[1];
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(null, "Test Ingredient");
        dishIngredientDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId(anyString(), anyString())).
                thenReturn(Optional.of(new Ingredient()));
        when(dishRepository.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        Dish result = dishService.createDish(dishIngredientDTO);

        assertEquals(1, result.getDishIngredients().size());
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    void createDish_NotFound_WithoutIngredientId() {
        IngredientIdQuantityDTO[] ingredientIdQuantityDTOS = new IngredientIdQuantityDTO[1];
        ingredientIdQuantityDTOS[0] = new IngredientIdQuantityDTO(null, "Test Ingredient");
        dishIngredientDTO.setIngredientIdQuantityList(ingredientIdQuantityDTOS);
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId(anyString(), anyString())).
                thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.createDish(dishIngredientDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ingredient with name Test Ingredient not found"));
    }

    @Test
    void updateDish_Success() {
        // Mock dependency
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testDish));

        // Prepare updated dish data
        Dish updatedDish = new Dish();
        updatedDish.setDishId(1L);
        updatedDish.setDishName("Updated Test Dish");
        updatedDish.setFirebaseId(TEST_FIREBASE_ID);

        // Prepare updated DTO
        DishIngredientDTO updatedDTO = new DishIngredientDTO(updatedDish, null);

        ArgumentCaptor<Dish> dishCaptor = ArgumentCaptor.forClass(Dish.class);
        when(dishRepository.save(dishCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        // Execute test
        Dish result = dishService.updateDish(1L, updatedDTO);

        // Verify captured dish
        Dish capturedDish = dishCaptor.getValue();
        assertEquals("Updated Test Dish", capturedDish.getDishName());
        verify(dishRepository).save(eq(updatedDish));
    }

    @Test
    void updateDish_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(99L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.updateDish(99L, dishIngredientDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Dish with id 99 not found"));
    }

    @Test
    void deleteDishes_Success() {
        DishRecord dishRecord = new DishRecord(1L, ZonedDateTime.now(), "Test Record", testDish, null, testDish.getFirebaseId());
        testDish.setDishRecords(new HashSet<>(Collections.singletonList(dishRecord)));
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testDish));

        dishService.deleteDishes(Collections.singletonList(1L));

        verify(dishRepository).deleteById(1L);
        verify(dishRecordRepository, times(testDish.getDishRecords().size())).save(any());
    }

    @Test
    void deleteDishes_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.findByDishIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.deleteDishes(Collections.singletonList(1L)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Dish with id 1 not found"));
    }

    @Test
    void searchDishes_ValidQuery() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(dishRepository.searchDishesByFirebaseId("test", TEST_FIREBASE_ID))
                .thenReturn(Collections.singletonList(testDish));

        List<Dish> results = dishService.searchDishes("test");

        assertEquals(1, results.size());
        assertEquals("Test Dish", results.get(0).getDishName());
    }

    @Test
    void saveDish_IngredientNotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(3L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        dishIngredientDTO.getIngredientIdQuantityList()[0].setIngredientId(3L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dishService.createDish(dishIngredientDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ingredient with id 3 not found"));
    }
}
