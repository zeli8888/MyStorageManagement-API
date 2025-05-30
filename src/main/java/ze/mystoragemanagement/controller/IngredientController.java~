package ze.mystoragemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.service.IngredientService;

import java.net.URI;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 21:38
 * @Version : V1.0
 * @Description :
 */

@RestController
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/ingredients/{ingredientId}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long ingredientId) {
        return ResponseEntity.ok(ingredientService.getIngredientById(ingredientId));
    }

    @GetMapping("/ingredients/name/{ingredientName}")
    public ResponseEntity<Ingredient> getIngredientByName(@PathVariable String ingredientName){
        return ResponseEntity.ok(ingredientService.getIngredientByName(ingredientName));
    }

    @PostMapping("/ingredients")
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient){
        Ingredient createdIngredient = ingredientService.createIngredient(ingredient);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{ingredientId}").buildAndExpand(createdIngredient.getIngredientId()).toUri();
        return ResponseEntity.created(uri).body(createdIngredient);
    }

    @DeleteMapping("/ingredients/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long ingredientId){
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ingredients/{ingredientId}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long ingredientId, @RequestBody Ingredient ingredient){
        return ResponseEntity.ok(ingredientService.updateIngredient(ingredientId, ingredient));
    }
}
