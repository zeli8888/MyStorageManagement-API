package ze.mystoragemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author : Ze Li
 * @Date : 18/08/2025 12:54
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private FirebaseApp firebaseApp;

    private static final ObjectMapper om = new ObjectMapper();

    @AfterEach
    void cleanup() {
        if (firebaseApp != null) {
            firebaseApp.delete();
        }
    }

    @Test
    public void testAuthentication_Success() throws Exception {
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);
        when(firebaseAuth.verifyIdToken(anyString(), eq(true)))
                .thenReturn(mockFirebaseToken);
        when(mockFirebaseToken.getUid()).thenReturn("test-user-id");
        HashMap<String, Object> claims = new HashMap<>() {
            {
                put("roles", List.of("admin"));
            }
        };
        when(mockFirebaseToken.getClaims()).thenReturn(claims);
        mockMvc.perform(get("/ingredients").header("Authorization", "Bearer test-token")).andExpect(status().isOk());
    }

    @Test
    public void testAuthentication_Fail() throws Exception {
        mockMvc.perform(get("/ingredients").header("Authorization", "wong-test-token")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/ingredients")).andExpect(status().isUnauthorized());
    }

    @Test
    public void testPublicResource() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
