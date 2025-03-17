package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.h2.engine.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String validUsername;
    private String invalidUsername;
    private UserEntity userEntity;
    private List<UserDetailsProjection> userDetailsProjections;

    @BeforeEach
    void setUp() throws Exception {
        validUsername = "valid";
        invalidUsername = "invalid";
        userEntity = UserFactory.createUserEntity();
        userDetailsProjections = UserDetailsFactory.createCustomClientUser(validUsername);


        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(userEntity));
        Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());
        Mockito.when(userUtil.getLoggedUsername()).thenReturn(validUsername);
        Mockito.when(repository.searchUserAndRolesByUsername(validUsername)).thenReturn(userDetailsProjections);
        Mockito.when(repository.searchUserAndRolesByUsername(invalidUsername)).thenReturn(List.of());
    }

    @Test
    public void authenticatedShouldReturnUserEntityWhenUserExists() {

        UserEntity result = service.authenticated();
        Assertions.assertNotNull(result);
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Mockito.when(userUtil.getLoggedUsername()).thenReturn(invalidUsername);


        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        UserDetails result = service.loadUserByUsername(validUsername);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(validUsername, result.getUsername());
        Assertions.assertEquals(userDetailsProjections.get(0).getPassword(), result.getPassword());
        Assertions.assertEquals(1, result.getAuthorities().size());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(invalidUsername);
        });
    }
}

