package ze.mystoragemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.model.DishRecord;
import ze.mystoragemanagement.security.AuthTokenFilter;
import ze.mystoragemanagement.service.DishRecordService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = DishRecordController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        ))
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DishRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishRecordService dishRecordService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private List<DishRecord> dishRecords;
    private DishRecordIngredientDTO dishRecordIngredientDTO;

    @BeforeEach
    void setUp() {
        // Initialize sample Dish
        Dish sampleDish = new Dish();
        sampleDish.setDishId(1L);
        sampleDish.setDishName("Tomato Soup");
        sampleDish.setDishDesc("Classic tomato based soup");

        // Initialize DishRecords
        dishRecords = Arrays.asList(
                new DishRecord(1L,
                        ZonedDateTime.parse("2024-03-20T12:00:00+08:00"),
                        "Lunch service record",
                        sampleDish,
                        Collections.emptySet(),
                        "firebase-123"
                ),
                new DishRecord(2L,
                        ZonedDateTime.parse("2024-03-20T18:30:00+08:00"),
                        "Dinner service record",
                        sampleDish,
                        Collections.emptySet(),
                        "firebase-456"
                )
        );

        // Initialize DTO
        dishRecordIngredientDTO = new DishRecordIngredientDTO(
                new DishRecord(
                        3L,
                        ZonedDateTime.parse("2024-03-21T09:45:00+08:00"),
                        "Breakfast service record",
                        sampleDish,
                        Collections.emptySet(),
                        "firebase-789"
                ),
                new IngredientIdQuantityDTO[] {
                        new IngredientIdQuantityDTO(101L, 2L),
                        new IngredientIdQuantityDTO(102L, 3L)
                }
        );
    }

    @Test
    void getAllDishRecords() throws Exception {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("dishRecordTime").descending());
        when(dishRecordService.getAllDishRecords(any(Pageable.class)))
                .thenReturn(new PageImpl<>(dishRecords));

        mockMvc.perform(get("/dishrecords")
                        .param("page", "0")
                        .param("size", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].dishRecordDesc", is("Lunch service record")))
                .andExpect(jsonPath("$.content[0].dish.dishName", is("Tomato Soup")));
    }

    @Test
    void searchDishRecords() throws Exception {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("dishRecordTime").descending());
        when(dishRecordService.searchDishRecords(eq("lunch"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(dishRecords.get(0))));

        mockMvc.perform(get("/dishrecords/search")
                        .param("searchString", "lunch")
                        .param("page", "0")
                        .param("size", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dishRecordDesc", containsString("Lunch")));
    }

    @Test
    void getDishRecordById() throws Exception {
        Long recordId = 1L;
        when(dishRecordService.getDishRecordById(recordId)).thenReturn(dishRecords.get(0));

        mockMvc.perform(get("/dishrecords/{dishRecordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dishRecordId", is(1)))
                .andExpect(jsonPath("$.firebaseId").doesNotExist()); // Verify @JsonIgnore
    }

    @Test
    void createDishRecord() throws Exception {
        DishRecord createdRecord = dishRecords.get(0);
        when(dishRecordService.createDishRecord(any(DishRecordIngredientDTO.class)))
                .thenReturn(createdRecord);

        mockMvc.perform(post("/dishrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishRecordIngredientDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.dishRecordTime", containsString("2024-03-20T12:00:00+08:00")));
    }

    @Test
    void updateDishRecord() throws Exception {
        Long recordId = 1L;
        DishRecord updatedRecord = new DishRecord(
                recordId,
                ZonedDateTime.parse("2024-03-20T12:30:00+08:00"),
                "Updated lunch record",
                dishRecords.get(0).getDish(),
                Collections.emptySet(),
                "firebase-123"
        );

        when(dishRecordService.updateDishRecord(eq(recordId), any(DishRecordIngredientDTO.class)))
                .thenReturn(updatedRecord);

        mockMvc.perform(put("/dishrecords/{dishRecordId}", recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishRecordIngredientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dishRecordDesc", is("Updated lunch record")));
    }

    @Test
    void deleteDishRecords() throws Exception {
        List<Long> recordIds = Arrays.asList(1L, 2L);
        doNothing().when(dishRecordService).deleteDishRecords(recordIds);

        mockMvc.perform(delete("/dishrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordIds)))
                .andExpect(status().isNoContent());
    }
}
