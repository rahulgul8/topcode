package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;

/**
 * Tests for UserController.
 */
public class UserControllerTest extends BaseControllerTest {

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {

    // No filter
    mockMvc.perform(get("/users").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(5))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.rows[0].email", equalTo("user1@doppler.com")))
        .andExpect(jsonPath("$.rows[0].fullName", equalTo("full name")))
        .andExpect(jsonPath("$.rows[0].division", equalTo("division")))
        .andExpect(jsonPath("$.rows[0].position", equalTo("position")))
        .andExpect(jsonPath("$.rows[0].location", equalTo("location")))
        .andExpect(jsonPath("$.rows[0].phoneNumber", equalTo("12345678")))
        .andExpect(jsonPath("$.rows[0].photoUrl", equalTo("http://example.com/photo.jpg")))
        .andExpect(jsonPath("$.rows[0].isNew", equalTo(false)))
        .andExpect(jsonPath("$.rows[0].points", equalTo(100)));

    // Paging
    mockMvc.perform(get("/users?limit=1&offset=2").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(1))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000005")));

    // Keyword
    mockMvc
        .perform(get("/users?limit=5&offset=0&keyword=dop").header("Authorization",
            super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(5))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000001")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Invalid paging
    mockMvc.perform(
        get("/users?limit=-0&offset=-1&keyword=dop").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/users")).andExpect(status().is(401));
  }
}
