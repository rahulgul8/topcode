package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.springframework.http.MediaType;
import com.doppler.entities.requests.LoginRequest;

/**
 * Tests for SecurityController.
 */
public class SecurityControllerTest extends BaseControllerTest {

  /**
   * Positive tests for login() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void login_200() throws Exception {
    // Existing user
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user1@doppler.com", "password"))))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.accessToken").exists()) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.user.email", equalTo("user1@doppler.com")))
        .andExpect(jsonPath("$.user.division", equalTo("division")))
        .andExpect(jsonPath("$.user.fullName", equalTo("full name")))
        .andExpect(jsonPath("$.user.location", equalTo("location")))
        .andExpect(jsonPath("$.user.phoneNumber", equalTo("12345678")))
        .andExpect(jsonPath("$.user.photoUrl", equalTo("http://example.com/photo.jpg")))
        .andExpect(jsonPath("$.user.position", equalTo("position")))
        .andExpect(jsonPath("$.user.isNew", equalTo(false)));

    // New user
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user-new@doppler.com", "password"))))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.accessToken").exists()) //
        .andExpect(jsonPath("$.user.id").exists()) //
        .andExpect(jsonPath("$.user.email", equalTo("user-new@doppler.com")))
        .andExpect(jsonPath("$.user.division", equalTo("division")))
        .andExpect(jsonPath("$.user.fullName", equalTo("full name")))
        .andExpect(jsonPath("$.user.location", equalTo("location")))
        .andExpect(jsonPath("$.user.phoneNumber", equalTo("12345678")))
        .andExpect(jsonPath("$.user.photoUrl", equalTo("http://example.com/photo.jpg")))
        .andExpect(jsonPath("$.user.position", equalTo("position")))
        .andExpect(jsonPath("$.user.isNew", equalTo(true)));
  }

  /**
   * Negative tests (bad request) for login() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void login_400() throws Exception {
    // Empty body
    mockMvc.perform(post("/login")).andExpect(status().is(400));

    // Missing email
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest("", "pasword"))))
        .andExpect(status().is(400));

    // Missing password
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user-new@doppler.com", null))))
        .andExpect(status().is(400));

    // Invalid email
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest("invalid", "password"))))
        .andExpect(status().is(400));
  }
}
