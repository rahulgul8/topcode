package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.springframework.http.MediaType;
import com.doppler.entities.UserTopic;

/**
 * Tests for UserTopicController.
 */
public class UserTopicControllerTest extends BaseControllerTest {

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {
    mockMvc.perform(get("/userTopics").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(4)))//
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$[0].topicId", equalTo("00000000-0000-0000-0001-000000000001")))
        .andExpect(jsonPath("$[0].userId", equalTo("00000000-0000-0000-0000-000000000001")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Unauthorized
    mockMvc.perform(get("/userTopics")).andExpect(status().is(401));
  }

  /**
   * Positive tests for update() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void update_200() throws Exception {
    List<UserTopic> userTopics = new ArrayList<>();

    UserTopic userTopic = new UserTopic();
    userTopic.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000001"));
    userTopics.add(userTopic);

    userTopic = new UserTopic();
    userTopic.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000002"));
    userTopics.add(userTopic);

    userTopic = new UserTopic();
    userTopic.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000003"));
    userTopics.add(userTopic);

    userTopic = new UserTopic();
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000005"));
    userTopics.add(userTopic);

    // isNew = false
    mockMvc.perform(post("/userTopics").header("Authorization", super.userBearerToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userTopics))).andExpect(status().is(200));

    // isNew = true
    mockMvc
        .perform(post("/userTopics").header("Authorization", super.user5BearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTopics)))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(
            jsonPath("$.description", equalTo("Congratulations! Your profile has been completed.")))
        .andExpect(jsonPath("$.points", equalTo(20))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.userId", equalTo("00000000-0000-0000-0000-000000000005")));
  }

  /**
   * Negative tests for update() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void update_40x() throws Exception {
    List<UserTopic> userTopics = new ArrayList<>();

    UserTopic userTopic = new UserTopic();
    userTopics.add(userTopic);

    // Null topicId
    mockMvc
        .perform(post("/userTopics").header("Authorization", super.userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTopics))) //
        .andExpect(status().is(400));

    // Non-existed topicId
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000009"));
    mockMvc
        .perform(post("/userTopics").header("Authorization", super.userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTopics))) //
        .andExpect(status().is(400));

    // Unauthorized
    userTopic.setTopicId(UUID.fromString("00000000-0000-0000-0001-000000000001"));
    mockMvc
        .perform(post("/userTopics").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTopics))) //
        .andExpect(status().is(401));
  }
}
