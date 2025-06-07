package ze.mystoragemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.dto.DishRecordIngredientDTO;
import ze.mystoragemanagement.dto.IngredientIdQuantityDTO;
import ze.mystoragemanagement.model.*;
import ze.mystoragemanagement.repository.DishRepository;
import ze.mystoragemanagement.service.DishRecordService;
import ze.mystoragemanagement.service.DishService;
import ze.mystoragemanagement.service.IngredientService;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyStorageManagementApplicationTests {

    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishRecordService dishRecordService;

    @Test
    void contextLoads() {
    }

    @Test
    void getAllDishes () {
        Ingredient ingredient1 = new Ingredient(null, "test1", 1L, 1L, "test");
        Ingredient ingredient2 = new Ingredient(null, "test2", 1L, 1L, "test");
        Ingredient ingredient3 = new Ingredient(null, "test3", 1L, 1L, "test");
        Ingredient ingredient4 = new Ingredient(null, "test4", 1L, 1L, "test");

        ingredientService.createIngredient(ingredient1);
        ingredientService.createIngredient(ingredient2);
        ingredientService.createIngredient(ingredient3);
        ingredientService.createIngredient(ingredient4);

        Dish dish1 = new Dish(null, "test1", "test1", new HashSet<>());
        Dish dish2 = new Dish(null, "test2", "test2", new HashSet<>());
        Dish dish3 = new Dish(null, "test3", "test3", new HashSet<>());
        Dish dish4 = new Dish(null, "test4", "test4", new HashSet<>());

        dishService.createDish(new DishIngredientDTO(dish1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(2L, 1L)
        }));

        dishService.createDish(new DishIngredientDTO(dish2, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(3L, 1L)
        }));

        dishService.getAllDishes();
    }

    @Test
    void deleteIngredient() {
        Ingredient ingredient1 = new Ingredient(null, "test1", 1L, 1L, "test");
        Ingredient ingredient2 = new Ingredient(null, "test2", 1L, 1L, "test");
        Ingredient ingredient3 = new Ingredient(null, "test3", 1L, 1L, "test");
        Ingredient ingredient4 = new Ingredient(null, "test4", 1L, 1L, "test");

        ingredientService.createIngredient(ingredient1);
        ingredientService.createIngredient(ingredient2);
        ingredientService.createIngredient(ingredient3);
        ingredientService.createIngredient(ingredient4);

        Dish dish1 = new Dish(null, "test1", "test1", new HashSet<>());
        Dish dish2 = new Dish(null, "test2", "test2", new HashSet<>());
        Dish dish3 = new Dish(null, "test3", "test3", new HashSet<>());
        Dish dish4 = new Dish(null, "test4", "test4", new HashSet<>());

        dishService.createDish(new DishIngredientDTO(dish1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(2L, 1L)
        }));

        dishService.createDish(new DishIngredientDTO(dish2, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(3L, 1L)
        }));

        DishRecord dishRecord1 = new DishRecord(null, LocalDateTime.now(), "testRecord1", dish1, new HashSet<>());
        DishRecord dishRecord2 = new DishRecord(null, LocalDateTime.now(), "testRecord2", dish2, new HashSet<>());
        DishRecord dishRecord3 = new DishRecord(null, LocalDateTime.now(), "testRecord3", dish3, new HashSet<>());
        DishRecord dishRecord4 = new DishRecord(null, LocalDateTime.now(), "testRecord4", dish4, new HashSet<>());
        DishRecord dishRecord5 = new DishRecord(null, LocalDateTime.now(), "testRecord5", dish4, new HashSet<>());

        dishRecordService.createDishRecord(new DishRecordIngredientDTO(dishRecord1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(2L, 1L)
        }));
        dishRecordService.createDishRecord(new DishRecordIngredientDTO(dishRecord2, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 2L), new IngredientIdQuantityDTO(4L, 2L)
        }));

        assertEquals(2, dishRecordService.getDishRecordById(1L).getDishRecordIngredients().size());
        assertEquals(2, dishRecordService.getDishRecordById(2L).getDishRecordIngredients().size());
        ingredientService.deleteIngredient(1L);

        assertNull(ingredientService.getIngredientById(1L));
        assertEquals(1, dishRecordService.getDishRecordById(1L).getDishRecordIngredients().size());
        assertEquals(1, dishRecordService.getDishRecordById(2L).getDishRecordIngredients().size());

        assertEquals(1, dishService.getDishByName("test1").getDishIngredients().size());
        assertEquals(1, dishService.getDishByName("test2").getDishIngredients().size());

        dishRecordService.deleteDishRecord(1L);
        assertNull(dishRecordService.getDishRecordById(1L));
        assertNotNull(dishService.getDishById(1L));

        dishService.updateDish(1L, new DishIngredientDTO(dish1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(2L, 1L), new IngredientIdQuantityDTO(3L, 1L)
        }));
        assertEquals(2, dishService.getDishByName("test1").getDishIngredients().size());

        dishService.updateDish(1L, new DishIngredientDTO(dish1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(2L, 1L)
        }));
        assertEquals(1, dishService.getDishByName("test1").getDishIngredients().size());
    }

    @Test
    void ingredientModify(){
        // update ingredient without dishIngredients attribute won't delete dishIngredients ==> orphanRemoval = false
        Ingredient ingredient1 = new Ingredient(null, "test1", 1L, 1L, "test");
        Ingredient ingredient2 = new Ingredient(null, "test2", 1L, 1L, "test");
        Ingredient ingredient3 = new Ingredient(null, "test3", 1L, 1L, "test");
        Ingredient ingredient4 = new Ingredient(null, "test4", 1L, 1L, "test");

        ingredientService.createIngredient(ingredient1);
        ingredientService.createIngredient(ingredient2);
        ingredientService.createIngredient(ingredient3);
        ingredientService.createIngredient(ingredient4);

        Dish dish1 = new Dish(null, "test1", "test1", new HashSet<>());
        Dish dish2 = new Dish(null, "test2", "test2", new HashSet<>());

        dishService.createDish(new DishIngredientDTO(dish1, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(1L, 1L), new IngredientIdQuantityDTO(2L, 1L)
        }));

        dishService.createDish(new DishIngredientDTO(dish2, new IngredientIdQuantityDTO[]{
                new IngredientIdQuantityDTO(3L, 1L)
        }));

        ingredientService.updateIngredient(ingredient1.getIngredientId(), new Ingredient(null, "test1-new", 1L, 1L, "test"));
        assertEquals(4, ingredientService.getAllIngredients().size());
        assertEquals(2, dishService.getDishByName("test1").getDishIngredients().size());
        assertEquals("test1-new", ingredientService.getIngredientById(1L).getIngredientName());

        // add new dishIngredient to DishIngredients for an ingredient, won't delete others ==> orphanRemoval = false
        Ingredient ingredientNew = new Ingredient(null, "test1-new", 1L, 1L, "test");
        HashSet<DishIngredient> dishIngredients = new HashSet<>();
        dishIngredients.add(new DishIngredient(
                new DishIngredientId(dish2.getDishId(),
                        ingredient1.getIngredientId()),
                3L,
                dish2,
                ingredientNew));
        ingredientNew.setDishIngredients(dishIngredients);
        assertEquals(1, dishService.getDishByName("test2").getDishIngredients().size());
        ingredientService.updateIngredient(ingredient1.getIngredientId(), ingredientNew);
        assertEquals(2, dishService.getDishByName("test1").getDishIngredients().size());
        assertEquals(2, dishService.getDishByName("test2").getDishIngredients().size());

        // update part of the DishIngredients for an ingredient, won't delete others ==> orphanRemoval = false
        Ingredient ingredientNew1 = new Ingredient(null, "test1-new", 1L, 1L, "test");
        HashSet<DishIngredient> dishIngredients1 = new HashSet<>();
        dishIngredients1.add(new DishIngredient(
                new DishIngredientId(dish1.getDishId(),
                        ingredient1.getIngredientId()),
                100L,
                dish1,
                ingredientNew1));
        ingredientNew1.setDishIngredients(dishIngredients1);
        assertEquals(2, dishService.getDishByName("test2").getDishIngredients().size());
        ingredientService.updateIngredient(ingredient1.getIngredientId(), ingredientNew1);
        assertEquals(2, dishService.getDishByName("test2").getDishIngredients().size());
        assertEquals(2, dishService.getDishByName("test1").getDishIngredients().size());
        ingredientService.deleteIngredient(ingredient2.getIngredientId());
        assertEquals(1, dishService.getDishByName("test1").getDishIngredients().size());
        assertEquals(100L, dishService.getDishByName("test1").getDishIngredients().iterator().next().getDishIngredientQuantity());
    }
}
