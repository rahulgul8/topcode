package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;

/**
 * Tests for RewardController.
 */
public class RewardControllerTest extends BaseControllerTest {

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {

    // No filter
    mockMvc.perform(get("/rewards").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(5))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.rows[0].title", equalTo("title 1")))
        .andExpect(jsonPath("$.rows[0].iconUrl", equalTo("http://doppler.com/reward1.jpg")))
        .andExpect(jsonPath("$.rows[0].pointsRequired", equalTo(10)));

    // Paging
    mockMvc.perform(get("/rewards?limit=1&offset=2").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(1))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000003")));

    // Sorting
    mockMvc
        .perform(get("/rewards?limit=5&offset=0&sortBy=pointsRequired&sortDirection=desc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(5))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000005")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Invalid paging
    mockMvc.perform(get("/rewards?limit=0&offset=-1&sortBy=pointsRequired&sortDirection=desc")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Invalid sortBy
    mockMvc.perform(get("/rewards?limit=1&offset=1&sortBy=id&sortDirection=desc")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Invalid sortDirection
    mockMvc.perform(get("/rewards?limit=1&offset=1&sortBy=title&sortDirection=invalid")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/users")).andExpect(status().is(401));
  }

  /**
   * Positive tests for redeem() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void redeem_200() throws Exception {
    mockMvc
        .perform(post("/rewards/00000000-0000-0000-0000-000000000001/redeem")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(jsonPath("$.reward.id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.expiredAt").exists());
  }

  /**
   * Negative tests for redeem() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void redeem_40x() throws Exception {

    // Invalid id
    mockMvc.perform(post("/rewards/invalid/redeem").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Not enough points
    mockMvc.perform(post("/rewards/00000000-0000-0000-0000-000000000001/redeem")
        .header("Authorization", super.user5BearerToken)).andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post("/rewards/00000000-0000-0000-0000-000000000001/redeem"))
        .andExpect(status().is(401));

    // Non-existed
    mockMvc.perform(post("/rewards/00000000-0000-0000-0000-000000000009/redeem")
        .header("Authorization", super.user5BearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for getAllCurrentUserRewards() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserRewards_200() throws Exception {
    mockMvc.perform(get("/myRewards").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(4))) //
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$[0].reward.id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$[0].expiredAt", equalTo("2019-10-01T00:00:00.000Z")));
  }

  /**
   * Negative tests for getAllCurrentUserRewards() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllCurrentUserRewards_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/myRewards")).andExpect(status().is(401));
  }
}
