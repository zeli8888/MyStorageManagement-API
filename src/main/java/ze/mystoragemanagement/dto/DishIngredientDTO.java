package ze.mystoragemanagement.dto;

import ze.mystoragemanagement.model.Dish;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 22:40
 * @Version : V1.0
 * @Description :
 */
public class DishIngredientDTO {
    private Dish dish;
    private IngredientIdQuantityDTO[] ingredientIdQuantityList;

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public IngredientIdQuantityDTO[] getIngredientIdQuantityList() {
        return ingredientIdQuantityList;
    }

    public void setIngredientIdQuantityList(IngredientIdQuantityDTO[] ingredientIdQuantityList) {
        this.ingredientIdQuantityList = ingredientIdQuantityList;
    }

    public DishIngredientDTO() {
    }

    public DishIngredientDTO(Dish dish, IngredientIdQuantityDTO[] ingredientIdQuantityList) {
        this.dish = dish;
        this.ingredientIdQuantityList = ingredientIdQuantityList;
    }
}

