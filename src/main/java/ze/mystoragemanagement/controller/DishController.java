package ze.mystoragemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ze.mystoragemanagement.dto.DishIngredientDTO;
import ze.mystoragemanagement.model.Dish;
import ze.mystoragemanagement.service.DishService;

import java.net.URI;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 21:13
 * @Version : V1.0
 * @Description :
 */

@RestController
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/dishes")
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @GetMapping("/dishes/{dishId}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long dishId) {
        return ResponseEntity.ok(dishService.getDishById(dishId));
    }

    @GetMapping("/dishes/name/{dishName}")
    public ResponseEntity<Dish> getDishByName(@PathVariable String dishName) {
        return ResponseEntity.ok(dishService.getDishByName(dishName));
    }


    @PostMapping("/dishes")
    public ResponseEntity<Dish> createDish(@RequestBody DishIngredientDTO dishIngredientDTO) {
        Dish createdDish = dishService.createDish(dishIngredientDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{dishId}").buildAndExpand(createdDish.getDishId()).toUri();
        return ResponseEntity.created(uri).body(createdDish);
    }

    @DeleteMapping("/dishes")
    public ResponseEntity<Void> deleteDishes(@RequestBody List<Long> dishIds) {
        dishService.deleteDishes(dishIds);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/dishes/{dishId}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long dishId, @RequestBody DishIngredientDTO dishIngredientDTO) {
        return ResponseEntity.ok(dishService.updateDish(dishId, dishIngredientDTO));
    }

    @GetMapping("/dishes/search")
    public ResponseEntity<List<Dish>> searchDishes(@RequestParam String searchString) {
        return ResponseEntity.ok(dishService.searchDishes(searchString));
    }
}
