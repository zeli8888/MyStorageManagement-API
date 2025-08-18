package ze.mystoragemanagement.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Modifier;
import static org.junit.jupiter.api.Assertions.*;

class ViewsTest {

    @Test
    void viewsClass_ShouldBePublicAndEmpty() throws Exception {
        Class<?> viewsClass = Views.class;
        assertTrue(Modifier.isPublic(viewsClass.getModifiers()));
    }

    @Test
    void dishRecordViewInnerClass_ShouldExistAndBePublicStatic() throws Exception {
        Views views = new Views();
        Class<?>[] innerClasses = views.getClass().getDeclaredClasses();

        String description = Views.DishRecordView.getDescription();
        assertEquals("dish record view for ignoring ingredients for dishes, but keep ingredients for records", description);
        assertTrue(innerClasses.length >= 1);

        Class<?> dishRecordViewClass = null;
        for (Class<?> clazz : innerClasses) {
            if (clazz.getSimpleName().equals("DishRecordView")) {
                dishRecordViewClass = clazz;
                break;
            }
        }
        assertNotNull(dishRecordViewClass);
        int modifiers = dishRecordViewClass.getModifiers();
        assertTrue(Modifier.isPublic(modifiers));
        assertTrue(Modifier.isStatic(modifiers));
    }
}
