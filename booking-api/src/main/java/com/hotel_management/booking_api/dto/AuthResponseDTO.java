package com.hotel_management.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

  private String token;
  private String tokenType = "Bearer";
  private Long expiresIn;
  private Long customerId;
  private String email;

  public AuthResponseDTO(String token, Long expiresIn, Long customerId, String email) {
    this.token = token;
    this.expiresIn = expiresIn;
    this.customerId = customerId;
    this.email = email;
  }
}
