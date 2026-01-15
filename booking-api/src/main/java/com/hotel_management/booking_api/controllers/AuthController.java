package com.hotel_management.booking_api.controllers;

import com.hotel_management.booking_api.dto.AuthResponseDTO;
import com.hotel_management.booking_api.dto.LoginRequestDTO;
import com.hotel_management.booking_api.dto.RegisterRequestDTO;
import com.hotel_management.booking_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "Register a new customer account")
  public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  @Operation(summary = "Login and get JWT token")
  public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
    AuthResponseDTO response = authService.login(request);
    return ResponseEntity.ok(response);
  }
}
