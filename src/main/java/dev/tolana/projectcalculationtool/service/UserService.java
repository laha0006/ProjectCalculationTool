package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.RegisterUserDto;
import dev.tolana.projectcalculationtool.dto.inviteDto;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;
    private AuthorizationRepository authorizationRepository;
    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, AuthorizationRepository authorizationRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.authorizationRepository = authorizationRepository;
    }

    public void registerUser(RegisterUserDto newUser) {
        UserDetails userToRegister = User.builder()
                .username(newUser.username())
                .password(passwordEncoder.encode(newUser.password())).roles("USER")
                .build();

        userDetailsManager.createUser(userToRegister);
    }

    public List<inviteDto> getInvitations(String username) {
        return authorizationRepository.getInvitations(username);
    }

    public void acceptInvite(String username, long organisationId) {
        authorizationRepository.addUserToOrganisation(username, organisationId);
    }

    public int getInvitationsCount(String username) {
        return getInvitations(username).size();
    }
}
