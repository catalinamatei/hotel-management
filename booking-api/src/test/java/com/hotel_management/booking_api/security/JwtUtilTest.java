package com.hotel_management.booking_api.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil("test-secret-key-for-testing-purposes-only-256bits", 86400000L);
  }

  @Test
  void generateToken_createsValidToken() {
    String token = jwtUtil.generateToken(1L, "test@example.com");

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void getEmailFromToken_returnsCorrectEmail() {
    String token = jwtUtil.generateToken(1L, "test@example.com");

    String email = jwtUtil.getEmailFromToken(token);

    assertThat(email).isEqualTo("test@example.com");
  }

  @Test
  void getCustomerIdFromToken_returnsCorrectId() {
    String token = jwtUtil.generateToken(42L, "test@example.com");

    Long customerId = jwtUtil.getCustomerIdFromToken(token);

    assertThat(customerId).isEqualTo(42L);
  }

  @Test
  void validateToken_withValidToken_returnsTrue() {
    String token = jwtUtil.generateToken(1L, "test@example.com");

    boolean isValid = jwtUtil.validateToken(token);

    assertThat(isValid).isTrue();
  }

  @Test
  void validateToken_withInvalidToken_returnsFalse() {
    boolean isValid = jwtUtil.validateToken("invalid.token.here");

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_withNullToken_returnsFalse() {
    boolean isValid = jwtUtil.validateToken(null);

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_withEmptyToken_returnsFalse() {
    boolean isValid = jwtUtil.validateToken("");

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_withTamperedToken_returnsFalse() {
    String token = jwtUtil.generateToken(1L, "test@example.com");
    String tamperedToken = token.substring(0, token.length() - 5) + "xxxxx";

    boolean isValid = jwtUtil.validateToken(tamperedToken);

    assertThat(isValid).isFalse();
  }

  @Test
  void getExpiration_returnsConfiguredValue() {
    assertThat(jwtUtil.getExpiration()).isEqualTo(86400000L);
  }
}
