package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.InviteFormDto;
import dev.tolana.projectcalculationtool.dto.RegisterUserDto;
import dev.tolana.projectcalculationtool.dto.InviteDto;
import dev.tolana.projectcalculationtool.exception.authorization.UserAlreadyInOrganisationException;
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

    public List<InviteDto> getInvitations(String username) {
        return authorizationRepository.getInvitations(username);
    }

    public void acceptInvite(String username, long organisationId) {
        authorizationRepository.addUserToOrganisation(username, organisationId);
    }

    public int getInvitationsCount(String username) {
        return getInvitations(username).size();
    }

    public void declineInvite(String username, long orgId) {
        authorizationRepository.removeInvite(username,orgId);
    }

    public boolean userInOrganisation(String username, long orgId) {
        return authorizationRepository.checkUserInOrganisation(username,orgId) == 1;
    }

    @PreAuthorize("@auth.hasOrgansiationAccess(#formData.organisationId()," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_INVITE)")
    public void createInvite(InviteFormDto formData) {
        String username = formData.username();
        long organisationId = formData.organisationId();
        if(userInOrganisation(username,organisationId)) {
            throw new UserAlreadyInOrganisationException();
        }
        authorizationRepository.createInvite(username,organisationId);
    }

    @PreAuthorize("@auth.hasOrgansiationAccess(#formData.organisationId()," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_INVITE)")
    public void removeInvite(InviteFormDto formData) {
        String username = formData.username();
        long organisationId = formData.organisationId();
        authorizationRepository.removeInvite(username,organisationId);
    }
}
