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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.security.AuthTokenFilter;
import ze.mystoragemanagement.service.DishService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = DishController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        )
)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Dish> dishes;
    private DishIngredientDTO dishIngredientDTO;

    @BeforeEach
    void setUp() {
        dishes = Arrays.asList(
                new Dish(1L, "Tomato Eggs", "fried eggs and tomato", null, null, "firebaseId1"),
                new Dish(2L, "Braised Beef", "beef with potatoes and carrots", null, null, "firebaseId2")
        );

        dishIngredientDTO = new DishIngredientDTO(
                dishes.get(0),
                new IngredientIdQuantityDTO[]{new IngredientIdQuantityDTO(1L, 2.0)}
        );
    }

    @Test
    void getAllDishes() throws Exception {
        when(dishService.getAllDishes()).thenReturn(dishes);

        mockMvc.perform(get("/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].dishName", is("Tomato Eggs")));
    }

    @Test
    void getDishById() throws Exception {
        Long dishId = 1L;
        when(dishService.getDishById(dishId)).thenReturn(dishes.get(0));

        mockMvc.perform(get("/dishes/{dishId}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dishId", is(dishId.intValue())))
                .andExpect(jsonPath("$.dishDesc", is("fried eggs and tomato")));
    }

    @Test
    void getDishByName() throws Exception {
        String dishName = "Braised Beef";
        when(dishService.getDishByName(dishName)).thenReturn(dishes.get(1));

        mockMvc.perform(get("/dishes/name/{dishName}", dishName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dishName", is(dishName)));
    }

    @Test
    void createDish() throws Exception {
        when(dishService.createDish(any(DishIngredientDTO.class))).thenReturn(dishes.get(0));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishIngredientDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.dishName", is("Tomato Eggs")));
    }

    @Test
    void deleteDishes() throws Exception {
        List<Long> dishIds = Arrays.asList(1L, 2L);
        doNothing().when(dishService).deleteDishes(dishIds);

        mockMvc.perform(delete("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishIds)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateDish() throws Exception {
        Long dishId = 1L;
        dishes.get(0).setDishName("Tomato Eggs Updated");

        when(dishService.updateDish(eq(dishId), any(DishIngredientDTO.class)))
                .thenReturn(dishes.get(0));

        mockMvc.perform(put("/dishes/{dishId}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishIngredientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dishName", is("Tomato Eggs Updated")));
    }

    @Test
    void searchDishes() throws Exception {
        String searchString = "Eggs";
        List<Dish> results = Collections.singletonList(dishes.get(0));

        when(dishService.searchDishes(searchString)).thenReturn(results);

        mockMvc.perform(get("/dishes/search")
                        .param("searchString", searchString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dishName", containsString("Tomato Eggs")));
    }
}
