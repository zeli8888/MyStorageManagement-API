package ze.mystoragemanagement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ze.mystoragemanagement.model.User;

/**
 * @Author : Ze Li
 * @Date : 05/07/2025 16:57
 * @Version : V1.0
 * @Description :
 */
public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    User getUser();
    void deleteUser();
    String updateUserPicture(MultipartFile file);
    Resource getUserPicture(String filename);
}
