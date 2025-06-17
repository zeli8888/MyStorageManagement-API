package ze.mystoragemanagement.service;

import ze.mystoragemanagement.model.Ingredient;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 19:23
 * @Version : V1.0
 * @Description :
 */
public interface IngredientService {
    List<Ingredient> getAllIngredients();

    Ingredient getIngredientById(Long id);

    Ingredient getIngredientByName(String ingredientName);

    void deleteIngredient(Long id);

    Ingredient createIngredient(Ingredient ingredient);

    Ingredient updateIngredient(Long ingredientId, Ingredient ingredient);

    List<Ingredient> searchIngredients(String searchString);
}
