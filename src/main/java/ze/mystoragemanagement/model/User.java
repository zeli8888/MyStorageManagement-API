package ze.mystoragemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 17:05
 * @Version : V1.0
 * @Description :
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name="user_id")
    @JsonIgnore
    private Long id;

    @Column(name="google_id", unique = true, nullable = true)
    @JsonIgnore
    private String googleId;

    @Column(name="password", nullable = true)
    private String password;

    @Column(name="user_email", unique = true, nullable = true)
    private String email;

    @Column(name="username", unique = true, nullable = true)
    private String username;

    @Column(name="user_picture", nullable = true)
    private String userPicture;

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
