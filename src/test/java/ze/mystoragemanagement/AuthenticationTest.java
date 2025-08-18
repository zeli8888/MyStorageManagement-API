package ze.mystoragemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ze.mystoragemanagement.model.Ingredient;
import java.util.HashMap;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @AfterAll
    public static void cleanup() {
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    @Test
    public void testAuthentication_Success() throws Exception {
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);
        when(firebaseAuth.verifyIdToken(anyString(), eq(false)))
                .thenReturn(mockFirebaseToken);
        when(mockFirebaseToken.getUid()).thenReturn("test-user-id");
        HashMap<String, Object> claims = new HashMap<>() {
            {
                put("roles", List.of("admin"));
            }
        };
        when(mockFirebaseToken.getClaims()).thenReturn(claims);
        mockMvc.perform(get("/dishrecords").header("Authorization", "Bearer test-token")).andExpect(status().isOk());
    }

    @Test
    public void testAuthentication_Success_Post() throws Exception {
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

        Ingredient newIngredient = new Ingredient(1L,
                "Flour",
                500L,
                1L,
                "Flour 1 euro per kg",
                null,
                null,
                "firebaseId1");

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)).header("Authorization", "Bearer test-token"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/ingredients/1")))
                .andExpect(jsonPath("$.ingredientId", is(1)))
                .andExpect(jsonPath("$.ingredientName", is("Flour")));
    }

    @Test
    public void testAuthentication_Fail() throws Exception {
        mockMvc.perform(get("/ingredients").header("Authorization", "wong-test-token")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/ingredients")).andExpect(status().isUnauthorized());
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);
        when(firebaseAuth.verifyIdToken(anyString(), eq(false)))
                .thenThrow(new IllegalArgumentException("Invalid token."));
        mockMvc.perform(get("/ingredients").header("Authorization", "Bearer test-token")).andExpect(status().isUnauthorized());
    }

    @Test
    public void testPublicResource() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mockMvc.perform(options("/ingredients")).andExpect(status().isOk());
    }
}
