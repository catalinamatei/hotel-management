package com.hotel_management.booking_api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.hotel_management.booking_api.dto.AuthResponseDTO;
import com.hotel_management.booking_api.dto.LoginRequestDTO;
import com.hotel_management.booking_api.dto.RegisterRequestDTO;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.repository.CustomerRepository;
import com.hotel_management.booking_api.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private CustomerRepository customerRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtil jwtUtil;

  @InjectMocks private AuthService authService;

  private RegisterRequestDTO registerRequest;
  private LoginRequestDTO loginRequest;
  private CustomerEntity customer;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequestDTO("John", "Doe", "john@example.com", "password123");
    loginRequest = new LoginRequestDTO("john@example.com", "password123");

    customer = new CustomerEntity();
    customer.setId(1L);
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john@example.com");
    customer.setPassword("encodedPassword");
  }

  @Test
  void register_withValidData_returnsAuthResponse() {
    when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);
    when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
    when(jwtUtil.getExpiration()).thenReturn(86400000L);

    AuthResponseDTO response = authService.register(registerRequest);

    assertThat(response.getToken()).isEqualTo("jwt-token");
    assertThat(response.getEmail()).isEqualTo("john@example.com");
    assertThat(response.getCustomerId()).isEqualTo(1L);
    verify(customerRepository).save(any(CustomerEntity.class));
  }

  @Test
  void register_withDuplicateEmail_throwsException() {
    when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

    assertThatThrownBy(() -> authService.register(registerRequest))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Email already registered");

    verify(customerRepository, never()).save(any());
  }

  @Test
  void login_withValidCredentials_returnsAuthResponse() {
    when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
    when(jwtUtil.getExpiration()).thenReturn(86400000L);

    AuthResponseDTO response = authService.login(loginRequest);

    assertThat(response.getToken()).isEqualTo("jwt-token");
    assertThat(response.getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void login_withInvalidEmail_throwsBadCredentials() {
    when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid email or password");
  }

  @Test
  void login_withInvalidPassword_throwsBadCredentials() {
    when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid email or password");
  }
}
