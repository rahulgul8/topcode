package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;

/**
 * Tests for TopicController.
 */
public class TopicControllerTest extends BaseControllerTest {

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {
    mockMvc.perform(get("/topics").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(5)))//
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0001-000000000001")))
        .andExpect(jsonPath("$[0].name", equalTo("name 1")))
        .andExpect(jsonPath("$[0].iconUrl", equalTo("http://doppler.com/icon1.jpg")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/topics")).andExpect(status().is(401));
  }
}
