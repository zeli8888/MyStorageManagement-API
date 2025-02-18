package ze.mystoragemanagement.dto;

/**
 * @Author : Ze Li
 * @Date : 17/02/2025 22:55
 * @Version : V1.0
 * @Description :
 */
public class IngredientIdQuantityDTO {
    private Long ingredientId;
    private Long quantity;

    public IngredientIdQuantityDTO(Long ingredientId, Long quantity) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public IngredientIdQuantityDTO() {
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
