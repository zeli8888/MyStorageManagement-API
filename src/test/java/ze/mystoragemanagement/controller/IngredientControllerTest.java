package ze.mystoragemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.security.AuthTokenFilter;
import ze.mystoragemanagement.service.IngredientService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = IngredientController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        )
)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientService ingredientService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Ingredient> ingredients;

    @BeforeEach
    void setUp() {
        ingredients = Arrays.asList(
                new Ingredient(1L,
                        "Sugar",
                        100L,
                        1L,
                        "Suagr 1 euro per kg",
                        null,
                        null,
                        "firebaseId1"),
                new Ingredient(2L,
                        "Salt",
                        200L,
                        1L,
                        "Salt 1 euro per kg",
                        null,
                        null,
                        "firebaseId1")
        );
    }

    @Test
    void getAllIngredients() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(ingredients);

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ingredientName", is("Sugar")))
                .andExpect(jsonPath("$[1].ingredientName", is("Salt")));
    }

    @Test
    void getIngredientById() throws Exception {
        Long ingredientId = 1L;
        when(ingredientService.getIngredientById(ingredientId)).thenReturn(ingredients.get(0));

        mockMvc.perform(get("/ingredients/{ingredientId}", ingredientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredientId", is(ingredientId.intValue())))
                .andExpect(jsonPath("$.ingredientName", is("Sugar")));
    }

    @Test
    void getIngredientByName() throws Exception {
        String name = "Salt";
        when(ingredientService.getIngredientByName(name)).thenReturn(ingredients.get(1));

        mockMvc.perform(get("/ingredients/name/{ingredientName}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredientName", is(name)));
    }

    @Test
    void createIngredient() throws Exception {
        Ingredient newIngredient = new Ingredient(3L,
                "Flour",
                500L,
                1L,
                "Flour 1 euro per kg",
                null,
                null,
                "firebaseId1");
        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(newIngredient);

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/ingredients/3")))
                .andExpect(jsonPath("$.ingredientId", is(3)))
                .andExpect(jsonPath("$.ingredientName", is("Flour")));
    }

    @Test
    void deleteIngredients() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        doNothing().when(ingredientService).deleteIngredients(ids);

        mockMvc.perform(delete("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateIngredient() throws Exception {
        Long ingredientId = 1L;
        ingredients.get(0).setIngredientName("Sugar Updated");
        when(ingredientService.updateIngredient(eq(ingredientId), any(Ingredient.class)))
                .thenReturn(ingredients.get(0));

        mockMvc.perform(put("/ingredients/{ingredientId}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredients.get(0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredientId", is(ingredientId.intValue())))
                .andExpect(jsonPath("$.ingredientName", is("Sugar Updated")));
    }

    @Test
    void searchIngredients() throws Exception {
        String searchString = "su";
        List<Ingredient> results = Collections.singletonList(
                ingredients.get(0)
        );
        when(ingredientService.searchIngredients(searchString)).thenReturn(results);

        mockMvc.perform(get("/ingredients/search")
                        .param("searchString", searchString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ingredientName", is("Sugar")));
    }
}
