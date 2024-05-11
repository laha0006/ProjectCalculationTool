package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.RegisterUserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;
    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterUserDto newUser) {
        UserDetails userToRegister = User.builder()
                .username(newUser.username())
                .password(passwordEncoder.encode(newUser.password())).roles("USER")
                .build();

        userDetailsManager.createUser(userToRegister);
    }
}
