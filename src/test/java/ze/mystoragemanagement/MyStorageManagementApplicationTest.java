package ze.mystoragemanagement;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @Author : Ze Li
 * @Date : 18/08/2025 22:47
 * @Version : V1.0
 * @Description :
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MyStorageManagementApplicationTest {

    @AfterAll
    public static void cleanup() {
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    @Test
    void contextLoads() {
    }
}