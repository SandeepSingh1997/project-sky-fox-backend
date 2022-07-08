package com.booking.users;

import com.booking.exceptions.PasswordMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User savedUser = findUserByUsername(username);

        return new UserPrincipal(savedUser);
    }

    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) throws PasswordMismatchException {
        User user = findUserByUsername(username);
        if (!user.getPassword().equals(changePasswordRequest.getCurrentPassword()))
            throw new PasswordMismatchException("Entered current password is not matching with existing password");
        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);
    }
}
