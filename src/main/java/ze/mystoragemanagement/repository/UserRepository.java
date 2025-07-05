package ze.mystoragemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ze.mystoragemanagement.model.User;

import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 05/07/2025 16:48
 * @Version : V1.0
 * @Description :
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findByGoogleId(String googleId);
}