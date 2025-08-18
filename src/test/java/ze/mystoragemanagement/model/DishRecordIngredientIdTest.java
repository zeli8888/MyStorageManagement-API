package ze.mystoragemanagement.model;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class DishRecordIngredientIdTest {

    @Test
    void equals_SameIds_ShouldReturnTrue() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(1L, 10L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(1L, 10L);
        assertTrue(id1.equals(id2));
    }

    @Test
    void equals_SameI_ShouldReturnTrue() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(1L, 10L);
        assertTrue(id1.equals(id1));
    }

    @Test
    void equals_DifferentDishRecordId_ShouldReturnFalse() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(1L, 10L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(2L, 10L);
        assertFalse(id1.equals(id2));
    }

    @Test
    void equals_DifferentIngredientId_ShouldReturnFalse() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(1L, 10L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(1L, 20L);
        assertFalse(id1.equals(id2));
    }

    @Test
    void equals_NullObject_ShouldReturnFalse() {
        DishRecordIngredientId id = new DishRecordIngredientId(1L, 10L);
        assertFalse(id.equals(null));
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        DishRecordIngredientId id = new DishRecordIngredientId(1L, 10L);
        String other = "not_an_id";
        assertFalse(id.equals(other));
    }

    @Test
    void equals_BothIdsNull_ShouldReturnTrue() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(null, null);
        DishRecordIngredientId id2 = new DishRecordIngredientId(null, null);
        assertTrue(id1.equals(id2));
    }

    @Test
    void hashCode_SameIds_ShouldBeEqual() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(3L, 30L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(3L, 30L);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void hashCode_DifferentIds_ShouldBeDifferent() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(4L, 40L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(5L, 50L);
        assertNotEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void hashCode_NullValues_ShouldHandleGracefully() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(null, 60L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(6L, null);
        assertAll(
                () -> assertNotNull(id1.hashCode()),
                () -> assertNotNull(id2.hashCode())
        );
    }

    @Test
    void serialization_ShouldPreserveObjectState() throws Exception {
        DishRecordIngredientId original = new DishRecordIngredientId(7L, 70L);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(original);
        oos.flush();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        DishRecordIngredientId deserialized = (DishRecordIngredientId) ois.readObject();

        assertEquals(original, deserialized);
        assertEquals(original.hashCode(), deserialized.hashCode());
    }

    @Test
    void equals_OneFieldNull_ShouldReturnFalse() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(null, 80L);
        DishRecordIngredientId id2 = new DishRecordIngredientId(8L, 80L);
        assertFalse(id1.equals(id2));
    }

    @Test
    void equals_AsymmetricNulls_ShouldReturnFalse() {
        DishRecordIngredientId id1 = new DishRecordIngredientId(9L, null);
        DishRecordIngredientId id2 = new DishRecordIngredientId(9L, 90L);
        assertFalse(id1.equals(id2));
    }
}
