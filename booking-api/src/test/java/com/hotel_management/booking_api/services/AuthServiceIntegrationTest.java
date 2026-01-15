package com.hotel_management.booking_api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotel_management.booking_api.config.TestcontainersConfiguration;
import com.hotel_management.booking_api.dto.AuthResponseDTO;
import com.hotel_management.booking_api.dto.LoginRequestDTO;
import com.hotel_management.booking_api.dto.RegisterRequestDTO;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@Transactional
class AuthServiceIntegrationTest {

  @Autowired private AuthService authService;

  @Autowired private CustomerRepository customerRepository;

  @BeforeEach
  void setUp() {
    customerRepository.deleteAll();
  }

  @Test
  void register_withValidData_createsCustomerAndReturnsToken() {
    RegisterRequestDTO request =
        new RegisterRequestDTO("John", "Doe", "john@example.com", "password123");

    AuthResponseDTO response = authService.register(request);

    assertThat(response.getToken()).isNotNull();
    assertThat(response.getEmail()).isEqualTo("john@example.com");
    assertThat(response.getCustomerId()).isNotNull();

    CustomerEntity saved = customerRepository.findByEmail("john@example.com").orElseThrow();
    assertThat(saved.getFirstName()).isEqualTo("John");
    assertThat(saved.getLastName()).isEqualTo("Doe");
    assertThat(saved.getPassword()).isNotEqualTo("password123"); // should be hashed
  }

  @Test
  void register_withDuplicateEmail_throwsException() {
    RegisterRequestDTO request =
        new RegisterRequestDTO("John", "Doe", "duplicate@example.com", "password123");
    authService.register(request);

    RegisterRequestDTO duplicate =
        new RegisterRequestDTO("Jane", "Doe", "duplicate@example.com", "password456");

    assertThatThrownBy(() -> authService.register(duplicate))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Email already registered");
  }

  @Test
  void login_withValidCredentials_returnsToken() {
    RegisterRequestDTO registerRequest =
        new RegisterRequestDTO("Jane", "Doe", "jane@example.com", "password123");
    authService.register(registerRequest);

    LoginRequestDTO loginRequest = new LoginRequestDTO("jane@example.com", "password123");

    AuthResponseDTO response = authService.login(loginRequest);

    assertThat(response.getToken()).isNotNull();
    assertThat(response.getEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void login_withWrongPassword_throwsBadCredentials() {
    RegisterRequestDTO registerRequest =
        new RegisterRequestDTO("Bob", "Smith", "bob@example.com", "password123");
    authService.register(registerRequest);

    LoginRequestDTO loginRequest = new LoginRequestDTO("bob@example.com", "wrongpassword");

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid email or password");
  }

  @Test
  void login_withNonExistentEmail_throwsBadCredentials() {
    LoginRequestDTO loginRequest = new LoginRequestDTO("nonexistent@example.com", "password123");

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid email or password");
  }
}
