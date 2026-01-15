package com.hotel_management.booking_api.services;

import com.hotel_management.booking_api.dto.AuthResponseDTO;
import com.hotel_management.booking_api.dto.LoginRequestDTO;
import com.hotel_management.booking_api.dto.RegisterRequestDTO;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.repository.CustomerRepository;
import com.hotel_management.booking_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthResponseDTO register(RegisterRequestDTO request) {
    if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalStateException("Email already registered");
    }

    CustomerEntity customer = new CustomerEntity();
    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setEmail(request.getEmail());
    customer.setPassword(passwordEncoder.encode(request.getPassword()));

    CustomerEntity saved = customerRepository.save(customer);

    String token = jwtUtil.generateToken(saved.getId(), saved.getEmail());
    return new AuthResponseDTO(token, jwtUtil.getExpiration(), saved.getId(), saved.getEmail());
  }

  public AuthResponseDTO login(LoginRequestDTO request) {
    CustomerEntity customer =
        customerRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(customer.getId(), customer.getEmail());
    return new AuthResponseDTO(
        token, jwtUtil.getExpiration(), customer.getId(), customer.getEmail());
  }
}
