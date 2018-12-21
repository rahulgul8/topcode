package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;

/**
 * Tests for ProfileController.
 */
public class ProfileControllerTest extends BaseControllerTest {

  /**
   * Positive tests for getAllCurrentUserBadges() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserBadges_200() throws Exception {
    mockMvc.perform(get("/myBadges").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(5))) //
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0003-000000000001")))
        .andExpect(jsonPath("$[0].title", equalTo("title 1")))
        .andExpect(jsonPath("$[0].iconUrl", equalTo("http://doppler.com/badge1.jpg")))
        .andExpect(jsonPath("$[0].gainedAt", equalTo("2018-10-10T00:00:00.000Z")));
  }

  /**
   * Negative tests for getAllCurrentUserBadges() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserBadges_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/myBadges")).andExpect(status().is(401));
  }

  /**
   * Positive tests for getAllCurrentUserRewardPoints() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserRewardPoints_200() throws Exception {
    mockMvc.perform(get("/myRewardPoints").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(4))) //
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$[0].description", equalTo("reward 1")))
        .andExpect(jsonPath("$[0].points", equalTo(1)))
        .andExpect(jsonPath("$[0].createdAt", equalTo("2018-10-01T00:00:00.000Z")))
        .andExpect(jsonPath("$[0].userId", equalTo("00000000-0000-0000-0000-000000000001")));
  }

  /**
   * Negative tests for getAllCurrentUserRewardPoints() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserRewardPoints_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/myRewardPoints")).andExpect(status().is(401));
  }
}
