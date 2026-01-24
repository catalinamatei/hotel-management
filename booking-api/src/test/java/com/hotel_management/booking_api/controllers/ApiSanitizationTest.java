package com.hotel_management.booking_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotel_management.booking_api.config.TestcontainersConfiguration;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
class ApiSanitizationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private RequestMappingHandlerMapping requestMappingHandlerMapping;

  private static final Set<String> EXPECTED_ENDPOINTS =
      Set.of(
          // Auth endpoints
          "POST /api/auth/register",
          "POST /api/auth/login",
          // Room endpoints
          "GET /api/room",
          "POST /api/room",
          "GET /api/room/{id}",
          "PUT /api/room/{id}",
          "DELETE /api/room/{id}",
          // Reservation endpoints
          "GET /api/reservations",
          "POST /api/reservations",
          "GET /api/reservations/{id}",
          "DELETE /api/reservations/{id}",
          "GET /api/reservations/free-intervals",
          // Health endpoint
          "GET /api/health");

  @Test
  void apiEndpoints_matchExpectedContract() {
    Set<String> actualEndpoints = new HashSet<>();

    for (var entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
      RequestMappingInfo mappingInfo = entry.getKey();
      HandlerMethod handlerMethod = entry.getValue();

      String controllerPackage = handlerMethod.getBeanType().getPackageName();
      if (!controllerPackage.startsWith("com.hotel_management.booking_api")) {
        continue;
      }

      Set<String> patterns = mappingInfo.getPatternValues();
      Set<String> methods =
          new HashSet<>(
              mappingInfo.getMethodsCondition().getMethods().stream().map(Enum::name).toList());

      if (methods.isEmpty()) {
        methods.add("GET");
      }

      for (String pattern : patterns) {
        for (String method : methods) {
          actualEndpoints.add(method + " " + pattern);
        }
      }
    }

    assertThat(actualEndpoints)
        .as("API endpoints should match expected contract")
        .containsExactlyInAnyOrderElementsOf(EXPECTED_ENDPOINTS);
  }

  @Test
  void authEndpoints_arePublic() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType("application/json")
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType("application/json")
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void healthEndpoint_isPublic() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/health"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void protectedEndpoints_requireAuth() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/room"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/reservations"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
