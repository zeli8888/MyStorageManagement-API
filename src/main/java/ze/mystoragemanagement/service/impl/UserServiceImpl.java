package ze.mystoragemanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ze.mystoragemanagement.model.User;
import ze.mystoragemanagement.repository.UserRepository;
import ze.mystoragemanagement.security.CustomUserDetails;
import ze.mystoragemanagement.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 05/07/2025 16:57
 * @Version : V1.0
 * @Description :
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${user.picture.size}")
    private int userPictureSize;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (userName == null) throw new UsernameNotFoundException("Please offer user name or email!");
        Optional<User> opt = userRepository.findByUserName(userName);

        if (!opt.isPresent()) {
            opt = userRepository.findByEmail(userName);
        }

        if(!opt.isPresent())
            throw new UsernameNotFoundException("User not exist!");
        else {
            User user = opt.get();
            if (user.getPassword() == null) throw new UsernameNotFoundException("Please login through third-party Account!");
            Set<GrantedAuthority> authorities = new HashSet<>();
            // Add a default role directly without the ROLE_ prefix
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new CustomUserDetails(
                    user.getUserName(),
                    user.getPassword(),
                    user.getId(),
                    authorities
            );
        }
    }
}
