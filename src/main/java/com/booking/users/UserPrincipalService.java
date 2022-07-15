package com.booking.users;

import com.booking.exceptions.PasswordMatchesWithLastThreePasswordsException;
import com.booking.exceptions.PasswordMismatchException;
import com.booking.passwordHistory.PasswordHistoryService;
import com.booking.users.repository.User;
import com.booking.users.repository.UserRepository;
import com.booking.users.view.ChangePasswordRequest;
import com.booking.users.view.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.booking.passwordHistory.repository.Constants.THREE;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordHistoryService passwordHistoryService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserPrincipalService(UserRepository userRepository, PasswordHistoryService passwordHistoryService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.passwordHistoryService = passwordHistoryService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User savedUser = findUserByUsername(username);

        return new UserPrincipal(savedUser);
    }

    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) throws Exception {
        User user = findUserByUsername(username);
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword()))
            throw new PasswordMismatchException("Entered current password is not matching with existing password");

        List<String> lastThreePasswords = passwordHistoryService.findRecentPasswordsByUserId(user.getId(), THREE);
        for (String password : lastThreePasswords) {
            if (bCryptPasswordEncoder.matches(changePasswordRequest.getNewPassword(), password))
                throw new PasswordMatchesWithLastThreePasswordsException("Entered new password matches with recent three passwords");
        }

        String hashedNewPassword = bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword());
        passwordHistoryService.add(user.getId(), hashedNewPassword);

        user.setPassword(hashedNewPassword);
        userRepository.save(user);
    }
}
