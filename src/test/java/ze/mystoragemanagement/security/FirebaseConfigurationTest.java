package ze.mystoragemanagement.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FirebaseConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FirebaseConfigurationTest {

    @Autowired
    private FirebaseApp firebaseApp;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @AfterAll
    public static void cleanup() {
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    @Test
    void firebaseAppBean_ShouldInitializeCorrectly() {
        assertThat(firebaseApp).isNotNull();
        assertThat(firebaseApp.getName()).isEqualTo(FirebaseApp.DEFAULT_APP_NAME);
    }

    @Test
    void firebaseAuthBean_ShouldBeCreatedFromFirebaseApp() {
        assertThat(firebaseAuth).isNotNull();
        assertThat(FirebaseAuth.getInstance(firebaseApp))
                .isSameAs(firebaseAuth);
    }
}
