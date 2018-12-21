package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;

/**
 * Tests for NotificationController.
 */
public class NotificationControllerTest extends BaseControllerTest {

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {
    mockMvc.perform(get("/notifications").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(3))) //
        .andExpect(jsonPath("$.rows", hasSize(3))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000005")))
        .andExpect(jsonPath("$.rows[0].iconUrl", equalTo("icon 5")))
        .andExpect(jsonPath("$.rows[0].content", equalTo("content 5")))
        .andExpect(jsonPath("$.rows[0].createdAt", equalTo("2018-10-05T00:00:00.000Z")))
        .andExpect(jsonPath("$.rows[0].read", equalTo(false)))
        .andExpect(jsonPath("$.rows[0].relatedObjectType", equalTo(null)))
        .andExpect(jsonPath("$.rows[0].relatedObjectId", equalTo(null)));

    // Paging
    mockMvc
        .perform(
            get("/notifications?limit=1&offset=2").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(3))) //
        .andExpect(jsonPath("$.rows", hasSize(1))) //
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
    mockMvc
        .perform(
            get("/notifications?limit=0&offset=-1").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/notifications")).andExpect(status().is(401));
  }

  /**
   * Positive tests for markAsRead() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void markAsRead_200() throws Exception {
    mockMvc
        .perform(post("/notifications/00000000-0000-0000-0000-000000000001/read")
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.iconUrl", equalTo("icon 1"))) //
        .andExpect(jsonPath("$.content", equalTo("content 1"))) //
        .andExpect(jsonPath("$.createdAt", equalTo("2018-10-01T00:00:00.000Z"))) //
        .andExpect(jsonPath("$.read", equalTo(true))) //
        .andExpect(jsonPath("$.relatedObjectType", equalTo(null))) //
        .andExpect(jsonPath("$.relatedObjectId", equalTo(null)));
  }

  /**
   * Negative tests for markAsRead() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void markAsRead_40x() throws Exception {

    // Invalid id
    mockMvc
        .perform(post("/notifications/invalid/read").header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post("/notifications/00000000-0000-0000-0000-000000000001/read"))
        .andExpect(status().is(401));

    // Other user's notification
    mockMvc
        .perform(post("/notifications/00000000-0000-0000-0000-000000000001/read")
            .header("Authorization", super.user5BearerToken)) //
        .andExpect(status().is(403));

    // Not found
    mockMvc
        .perform(post("/notifications/00000000-0000-0000-0000-000000000009/read")
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));
  }
}
