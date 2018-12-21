package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * Tests for UserSettingController.
 */
public class UserSettingControllerTest extends BaseControllerTest {

  /**
   * Positive tests for getSetting() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getSetting_200() throws Exception {
    mockMvc.perform(get("/userSettings").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.notifiedByNewEvents", equalTo(true)));
  }

  /**
   * Negative tests for getSetting() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getSetting_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/userSettings")).andExpect(status().is(401));
  }

  /**
   * Positive tests for updateSetting() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void updateSetting_200() throws Exception {
    String request = "{\"notifiedByNewEvents\": false }";

    mockMvc
        .perform(post("/userSettings").header("Authorization", super.userBearerToken)
            .contentType(MediaType.APPLICATION_JSON).content(request)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.notifiedByNewEvents", equalTo(false)));
  }

  /**
   * Negative tests for updateSetting() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void updateSetting_40x() throws Exception {

    // Null
    String request = "{\"notifiedByNewEvents\": null }";
    mockMvc
        .perform(post("/userSettings").header("Authorization", super.userBearerToken)
            .contentType(MediaType.APPLICATION_JSON).content(request)) //
        .andExpect(status().is(400));

    // Unauthorized
    request = "{\"notifiedByNewEvents\": false }";
    mockMvc.perform(post("/userSettings") //
        .contentType(MediaType.APPLICATION_JSON).content(request)) //
        .andExpect(status().is(401));
  }
}
