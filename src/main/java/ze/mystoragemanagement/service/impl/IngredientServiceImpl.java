package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.service.IngredientService;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 12/02/2025 19:24
 * @Version : V1.0
 * @Description :
 */

@Service
public class IngredientServiceImpl implements IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    @Override
    public Ingredient getIngredientByName(String ingredientName) {
        return ingredientRepository.findIngredientByIngredientName(ingredientName).orElse(null);
    }

    @Override
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    @Override
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient updateIngredient(Long ingredientId, Ingredient ingredient) {
        ingredient.setIngredientId(ingredientId);
        return ingredientRepository.save(ingredient);
    }
}
