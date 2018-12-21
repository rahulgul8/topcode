package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.doppler.entities.EventComment;
import com.doppler.entities.requests.EventCommentRequest;
import com.doppler.repositories.EventCommentRepository;

/**
 * Tests for EventCommentController.
 */
public class EventCommentControllerTest extends BaseControllerTest {

  /**
   * The event comment repository.
   */
  @Autowired
  private EventCommentRepository eventCommentRepository;

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {

    // No filter
    mockMvc
        .perform(get("/events/00000000-0000-0000-0002-000000000001/comments")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(2))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000002")))
        .andExpect(jsonPath("$.rows[0].content", equalTo("content 2")))
        .andExpect(jsonPath("$.rows[0].user.id", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.rows[0].parentCommentId", equalTo(null)))
        .andExpect(jsonPath("$.rows[0].likeCount", equalTo(1)))
        .andExpect(jsonPath("$.rows[0].createdAt").exists())
        .andExpect(jsonPath("$.rows[0].childComments", hasSize(0))) //
        .andExpect(jsonPath("$.rows[0].likedByMe", equalTo(true)))
        .andExpect(jsonPath("$.rows[1].childComments", hasSize(2)));

    // Paging
    mockMvc
        .perform(get("/events/00000000-0000-0000-0002-000000000001/comments?limit=1&offset=1")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(1))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000001")));

    // Paging and sorting
    mockMvc.perform(get(
        "/events/00000000-0000-0000-0002-000000000001/comments?limit=1&offset=1&sortBy=likeCount&sortDirection=desc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(1)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000002")));

    // Paging and sorting createdAt
    mockMvc.perform(get(
        "/events/00000000-0000-0000-0002-000000000001/comments?limit=1&offset=1&sortBy=createdAt&sortDirection=desc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(1)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0000-000000000001")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Invalid event id
    mockMvc.perform(get("/events/invalid/comments").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid paging
    mockMvc.perform(get(
        "/events/00000000-0000-0000-0002-000000000001/comments?limit=0&offset=-1&sortBy=likeCount&sortDirection=desc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid sortBy
    mockMvc.perform(get(
        "/events/00000000-0000-0000-0002-000000000001/comments?limit=1&offset=0&sortBy=invalid&sortDirection=desc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid sortDirection
    mockMvc.perform(get(
        "/events/00000000-0000-0000-0002-000000000001/comments?limit=1&offset=0&sortBy=createdAt&sortDirection=invalid")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000001/comments"))
        .andExpect(status().is(401));

    // Non-existed event id
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000009/comments")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for create() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void create_200() throws Exception {
    EventCommentRequest request = new EventCommentRequest();
    request.setContent("new content");

    // Root
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(jsonPath("$.content", equalTo("new content"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo(null))) //
        .andExpect(jsonPath("$.likeCount", equalTo(0))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(false)));

    // Child
    request.setContent("new content 2");
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(jsonPath("$.content", equalTo("new content 2"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.likeCount", equalTo(0))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(false)));
  }

  /**
   * Negative tests for create() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void create_40x() throws Exception {
    EventCommentRequest request = new EventCommentRequest();
    request.setContent("new content");

    // Invalid event id
    mockMvc.perform(post("/events/invalid/comments").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Missing content
    request.setContent("");
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Non-existed parentCommentId
    request.setContent("new content");
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // parentCommentId belongs to another event
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // More than 2 levels
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(401));

    // Not found
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000009/comments")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for update() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void update_200() throws Exception {
    EventCommentRequest request = new EventCommentRequest();
    request.setContent("content - updated");

    // Root
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000002")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000002")))
        .andExpect(jsonPath("$.content", equalTo("content - updated"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo(null))) //
        .andExpect(jsonPath("$.likeCount", equalTo(1))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(true)));

    // Child
    request.setContent("content - updated 2");
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000004")))
        .andExpect(jsonPath("$.content", equalTo("content - updated 2"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo("00000000-0000-0000-0000-000000000002"))) //
        .andExpect(jsonPath("$.likeCount", equalTo(0))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(false)));
  }

  /**
   * Negative tests for update() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void update_40x() throws Exception {
    EventCommentRequest request = new EventCommentRequest();
    request.setContent("new content");

    // Invalid event id
    mockMvc.perform(put("/events/invalid/comments/00000000-0000-0000-0000-000000000004")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Invalid comment id
    mockMvc.perform(put("/events/00000000-0000-0000-0002-000000000001/comments/invalid")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Missing content
    request.setContent("");
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Different event
    request.setContent("new content");
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000005")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Non-existed parentCommentId
    request.setContent("new content");
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // parentCommentId belongs to another event
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // More than 2 levels
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Sub of itself
    request.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(401));

    // Another user'comment
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(403));

    // Not found
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000009/comments/00000000-0000-0000-0000-000000000005")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(404));

    // Not found (comment)
    mockMvc.perform(put(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000009")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(404));
  }

  /**
   * Positive tests for like() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void like_200() throws Exception {
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000005"))) //
        .andExpect(jsonPath("$.content", equalTo("content 5"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000002"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo(null))) //
        .andExpect(jsonPath("$.likeCount", equalTo(1))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(true)));
  }

  /**
   * Negative tests for like() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void like_40x() throws Exception {

    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200));

    // Liked already
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Invalid event id
    mockMvc
        .perform(post("/events/invalid/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Invalid comment id
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000002/comments/invalid/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON)) //
        .andExpect(status().is(401));

    // Own comment
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000003/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(403));

    // Deleted comment
    EventComment eventComment = eventCommentRepository
        .findById(UUID.fromString("00000000-0000-0000-0000-000000000005")).get();
    eventComment.setDeleted(true);
    eventCommentRepository.save(eventComment);
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(403));

    // Non-existed comment
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000009/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));

    // Non-existed event
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000009/comments/00000000-0000-0000-0000-000000000001/like")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));
  }

  /**
   * Positive tests for unlike() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void unlike_200() throws Exception {
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000001/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.content", equalTo("content 1"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo(null))) //
        .andExpect(jsonPath("$.likeCount", equalTo(2))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(2))) //
        .andExpect(
            jsonPath("$.childComments[0].id", equalTo("00000000-0000-0000-0000-000000000003"))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(false)));
  }

  /**
   * Negative tests for unlike() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void unlike_40x() throws Exception {

    // Invalid event id
    mockMvc
        .perform(post("/events/invalid/comments/00000000-0000-0000-0000-000000000005/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Invalid comment id
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000002/comments/invalid/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/unlike")
            .contentType(MediaType.APPLICATION_JSON)) //
        .andExpect(status().is(401));

    // Deleted comment
    EventComment eventComment = eventCommentRepository
        .findById(UUID.fromString("00000000-0000-0000-0000-000000000005")).get();
    eventComment.setDeleted(true);
    eventCommentRepository.save(eventComment);
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(403));

    // Non-existed comment
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000009/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));

    // Non-existed event
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000009/comments/00000000-0000-0000-0000-000000000001/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));

    // Not liked yet
    mockMvc.perform(post(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004/unlike")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));
  }

  /**
   * Positive tests for delete() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void delete_200() throws Exception {
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0000-000000000004"))) //
        .andExpect(jsonPath("$.content", equalTo("This comment has been deleted"))) //
        .andExpect(jsonPath("$.user.id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.parentCommentId", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$.likeCount", equalTo(0))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.childComments", hasSize(0))) //
        .andExpect(jsonPath("$.deleted", equalTo(true))) //
        .andExpect(jsonPath("$.likedByMe", equalTo(false)));
  }

  /**
   * Negative tests for delete() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void delete_40x() throws Exception {

    // Invalid event id
    mockMvc
        .perform(delete("/events/invalid/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Invalid comment id
    mockMvc
        .perform(delete("/events/00000000-0000-0000-0002-000000000001/comments/invalid")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Different event
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000005")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000004")
            .contentType(MediaType.APPLICATION_JSON)) //
        .andExpect(status().is(401));

    // Other user's comment
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000002/comments/00000000-0000-0000-0000-000000000005")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(403));

    // Non-existed comment
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000001/comments/00000000-0000-0000-0000-000000000009")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));

    // Non-existed event
    mockMvc.perform(delete(
        "/events/00000000-0000-0000-0002-000000000009/comments/00000000-0000-0000-0000-000000000001")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", super.userBearerToken)) //
        .andExpect(status().is(404));
  }
}
