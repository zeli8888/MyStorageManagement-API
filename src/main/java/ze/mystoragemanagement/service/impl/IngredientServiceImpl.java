package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.model.Ingredient;
import ze.mystoragemanagement.repository.IngredientRepository;
import ze.mystoragemanagement.security.FirebaseSecurityContextId;
import ze.mystoragemanagement.service.IngredientService;

import java.util.Collection;
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
    private FirebaseSecurityContextId firebaseSecurityContextId;
    @Autowired
    private IngredientRepository ingredientRepository;

    private String getCurrentUserFirebaseId() {
        return firebaseSecurityContextId.getCurrentFirebaseId();
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAllByFirebaseId(getCurrentUserFirebaseId());
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findByIngredientIdAndFirebaseId(id, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with id " + id + " not found"));
    }

    @Override
    public Ingredient getIngredientByName(String ingredientName) {
        return ingredientRepository.findByIngredientNameAndFirebaseId(ingredientName, getCurrentUserFirebaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with name " + ingredientName + " not found"));
    }

    @Override
    @Transactional
    public void deleteIngredients(Collection<Long> ids) {
        String firebaseId = getCurrentUserFirebaseId();
        ingredientRepository.deleteAllByIdInAndFirebaseId(ids, firebaseId);
    }

    @Override
    public Ingredient createIngredient(Ingredient ingredient) {
        ingredient.setIngredientId(null);
        ingredient.setFirebaseId(getCurrentUserFirebaseId());
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient updateIngredient(Long ingredientId, Ingredient ingredient) {
        String firebaseId = getCurrentUserFirebaseId();
        ingredientRepository.findByIngredientIdAndFirebaseId(ingredientId, firebaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with id " + ingredientId + " not found"));

        ingredient.setIngredientId(ingredientId);
        ingredient.setFirebaseId(firebaseId);
        return ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> searchIngredients(String searchString) {
        return ingredientRepository.searchIngredientsByFirebaseId(searchString, getCurrentUserFirebaseId());
    }
}
