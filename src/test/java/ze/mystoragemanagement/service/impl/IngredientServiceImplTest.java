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
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private FirebaseSecurityContextId firebaseSecurityContextId;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private final String TEST_FIREBASE_ID = "test-firebase-123";
    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testIngredient = new Ingredient();
        testIngredient.setIngredientId(1L);
        testIngredient.setIngredientName("Flour");
        testIngredient.setIngredientStorage(500.0);
        testIngredient.setFirebaseId(TEST_FIREBASE_ID);
    }

    @Test
    void getAllIngredients_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findAllByFirebaseId(TEST_FIREBASE_ID))
                .thenReturn(Arrays.asList(testIngredient));

        List<Ingredient> result = ingredientService.getAllIngredients();

        assertEquals(1, result.size());
        assertEquals("Flour", result.get(0).getIngredientName());
        verify(ingredientRepository).findAllByFirebaseId(TEST_FIREBASE_ID);
    }

    @Test
    void getIngredientById_Found() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testIngredient));

        Ingredient result = ingredientService.getIngredientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIngredientId());
    }

    @Test
    void getIngredientById_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(2L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> ingredientService.getIngredientById(2L));

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains("Ingredient with id 2 not found"));
    }

    @Test
    void getIngredientByName_Found() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId("Flour", TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testIngredient));

        Ingredient result = ingredientService.getIngredientByName("Flour");

        assertEquals(1L, result.getIngredientId());
        assertEquals("Flour", result.getIngredientName());
    }

    @Test
    void getIngredientByName_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientNameAndFirebaseId("Unknown", TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> ingredientService.getIngredientByName("Unknown"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Ingredient with name Unknown not found", exception.getReason());
    }


    @Test
    void createIngredient_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(testIngredient);

        Ingredient newIngredient = new Ingredient();
        newIngredient.setIngredientName("Flour");

        Ingredient result = ingredientService.createIngredient(newIngredient);

        assertNotNull(result.getIngredientId());
        assertEquals(TEST_FIREBASE_ID, result.getFirebaseId());
        verify(ingredientRepository).save(newIngredient);
    }

    @Test
    void updateIngredient_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(1L, TEST_FIREBASE_ID))
                .thenReturn(Optional.of(testIngredient));

        ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
        when(ingredientRepository.save(ingredientCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        Ingredient updated = new Ingredient();
        updated.setIngredientName("Premium Flour");
        updated.setIngredientStorage(1000.0);

        Ingredient result = ingredientService.updateIngredient(1L, updated);

        Ingredient savedIngredient = ingredientCaptor.getValue();
        assertEquals("Premium Flour", savedIngredient.getIngredientName());
        assertEquals(1000L, savedIngredient.getIngredientStorage());
        assertEquals(TEST_FIREBASE_ID, savedIngredient.getFirebaseId());

        assertSame(savedIngredient, result);
    }

    @Test
    void updateIngredient_NotFound() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.findByIngredientIdAndFirebaseId(99L, TEST_FIREBASE_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> ingredientService.updateIngredient(99L, new Ingredient()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Ingredient with id 99 not found", exception.getReason());
    }

    @Test
    void deleteIngredients_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);

        ingredientService.deleteIngredients(Collections.singletonList(1L));

        verify(ingredientRepository).deleteAllByIdInAndFirebaseId(
                Collections.singletonList(1L),
                TEST_FIREBASE_ID
        );
    }

    @Test
    void searchIngredients_Success() {
        when(firebaseSecurityContextId.getCurrentFirebaseId()).thenReturn(TEST_FIREBASE_ID);
        when(ingredientRepository.searchIngredientsByFirebaseId("flo", TEST_FIREBASE_ID))
                .thenReturn(Collections.singletonList(testIngredient));

        List<Ingredient> results = ingredientService.searchIngredients("flo");

        assertEquals(1, results.size());
        assertEquals("Flour", results.get(0).getIngredientName());
    }
}
