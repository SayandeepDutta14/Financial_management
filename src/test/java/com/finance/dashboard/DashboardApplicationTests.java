package com.finance.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.dashboard.dto.AuthRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardApplicationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    static String adminToken;
    static String viewerToken;

    @Test @Order(1)
    void adminLoginShouldReturnToken() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andReturn();

        adminToken = objectMapper.readTree(result.getResponse().getContentAsString())
            .get("token").asText();
    }

    @Test @Order(2)
    void viewerLoginShouldReturnToken() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("viewer");
        req.setPassword("viewer123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("VIEWER"))
            .andReturn();

        viewerToken = objectMapper.readTree(result.getResponse().getContentAsString())
            .get("token").asText();
    }

    @Test @Order(3)
    void invalidLoginShouldReturn401() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("admin");
        req.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }

    @Test @Order(4)
    void adminCanCreateFinancialRecord() throws Exception {
        String body = """
            {
              "amount": 5000.00,
              "type": "INCOME",
              "category": "Salary",
              "date": "2024-04-01",
              "notes": "Monthly salary"
            }
            """;

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test @Order(5)
    void viewerCannotCreateFinancialRecord() throws Exception {
        String body = """
            {
              "amount": 100.00,
              "type": "EXPENSE",
              "category": "Food",
              "date": "2024-04-01"
            }
            """;

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + viewerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isForbidden());
    }

    @Test @Order(6)
    void viewerCanReadRecords() throws Exception {
        mockMvc.perform(get("/api/records")
                .header("Authorization", "Bearer " + viewerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test @Order(7)
    void viewerCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + viewerToken))
            .andExpect(status().isForbidden());
    }

    @Test @Order(8)
    void adminCanAccessDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalIncome").exists())
            .andExpect(jsonPath("$.totalExpenses").exists())
            .andExpect(jsonPath("$.netBalance").exists());
    }

    @Test @Order(9)
    void unauthenticatedRequestShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/records"))
            .andExpect(status().isUnauthorized());
    }

    @Test @Order(10)
    void createRecordWithInvalidDataShouldReturn400() throws Exception {
        String body = """
            {
              "amount": -100,
              "type": "INCOME",
              "category": "",
              "date": null
            }
            """;

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}
