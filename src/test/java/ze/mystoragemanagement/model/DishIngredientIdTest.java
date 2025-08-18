package ze.mystoragemanagement.model;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class DishIngredientIdTest {

    @Test
    void equals_ShouldReturnTrueForSameCompositeKey() {
        DishIngredientId id1 = new DishIngredientId(101L, 201L);
        DishIngredientId id2 = new DishIngredientId(101L, 201L);

        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
    }

    @Test
    void equals_ShouldReturnTrueForSame() {
        DishIngredientId id1 = new DishIngredientId(101L, 201L);
        assertTrue(id1.equals(id1));
    }

    @Test
    void equals_ShouldReturnFalseForDifferentDishId() {
        DishIngredientId id1 = new DishIngredientId(101L, 201L);
        DishIngredientId id2 = new DishIngredientId(102L, 201L);

        assertFalse(id1.equals(id2));
    }

    @Test
    void equals_ShouldReturnFalseForDifferentIngredientId() {
        DishIngredientId id1 = new DishIngredientId(101L, 201L);
        DishIngredientId id2 = new DishIngredientId(101L, 202L);

        assertFalse(id1.equals(id2));
    }

    @Test
    void equals_ShouldReturnFalseForNullObject() {
        DishIngredientId id = new DishIngredientId(101L, 201L);
        assertFalse(id.equals(null));
    }

    @Test
    void equals_ShouldReturnFalseForDifferentClass() {
        DishIngredientId id = new DishIngredientId(101L, 201L);
        Object other = "not_a_composite_key";
        assertFalse(id.equals(other));
    }

    @Test
    void hashCode_ShouldBeSameForSameKeys() {
        DishIngredientId id1 = new DishIngredientId(103L, 203L);
        DishIngredientId id2 = new DishIngredientId(103L, 203L);

        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void hashCode_ShouldBeDifferentForDifferentKeys() {
        DishIngredientId id1 = new DishIngredientId(105L, 205L);
        DishIngredientId id2 = new DishIngredientId(105L, 206L);

        assertNotEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void serialization_ShouldWorkCorrectly() throws Exception {
        DishIngredientId original = new DishIngredientId(107L, 207L);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(original);
        oos.flush();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        DishIngredientId deserialized = (DishIngredientId) ois.readObject();

        assertEquals(original, deserialized);
        assertEquals(original.hashCode(), deserialized.hashCode());
    }

    @Test
    void equals_ShouldHandleNullValues() {
        DishIngredientId id1 = new DishIngredientId(null, 209L);
        DishIngredientId id2 = new DishIngredientId(null, 209L);
        DishIngredientId id3 = new DishIngredientId(110L, null);

        assertTrue(id1.equals(id2));
        assertFalse(id1.equals(id3));
    }
}
