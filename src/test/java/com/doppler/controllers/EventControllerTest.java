package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.doppler.entities.Notification;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.repositories.NotificationRepository;

/**
 * Tests for EventController.
 */
public class EventControllerTest extends BaseControllerTest {

  /**
   * The notification repository.
   */
  @Autowired
  private NotificationRepository notificationRepository;

  /**
   * Positive tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_200() throws Exception {

    // No filter
    mockMvc.perform(get("/events").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(5))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000003")))
        .andExpect(jsonPath("$.rows[0].title", equalTo("title 3")))
        .andExpect(jsonPath("$.rows[0].description", equalTo("description 3")))
        .andExpect(jsonPath("$.rows[0].pointsForRegistering", equalTo(300)))
        .andExpect(jsonPath("$.rows[0].pointsForScanningTicket", equalTo(301)))
        .andExpect(jsonPath("$.rows[0].topicId", equalTo("00000000-0000-0000-0001-000000000002")))
        .andExpect(jsonPath("$.rows[0].location", equalTo("location 3")))
        .andExpect(jsonPath("$.rows[0].currentBooking", equalTo(30)))
        .andExpect(jsonPath("$.rows[0].maxCapacity", equalTo(50)))
        .andExpect(jsonPath("$.rows[0].waitingCapacity", equalTo(20)))
        .andExpect(jsonPath("$.rows[0].tags[0]", equalTo("tag 5")))
        .andExpect(
            jsonPath("$.rows[0].portraitImageUrl", equalTo("http://doppler.com/portrait3.jpg")))
        .andExpect(
            jsonPath("$.rows[0].landscapeImageUrl", equalTo("http://doppler.com/landscape3.jpg")))
        .andExpect(jsonPath("$.rows[0].sessions", hasSize(1)))
        .andExpect(jsonPath("$.rows[0].isMyEvent", equalTo(true)));

    // Paging
    mockMvc.perform(get("/events?limit=1&offset=1").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(1))) //
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000004")));

    // Paging and sorting
    mockMvc
        .perform(get("/events?limit=1&offset=1&sortBy=firstSessionStart&sortDirection=asc")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(5))) //
        .andExpect(jsonPath("$.rows", hasSize(1)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000004")));

    // topicId
    mockMvc.perform(get(
        "/events?limit=1&offset=0&sortBy=firstSessionStart&sortDirection=asc&topicId=00000000-0000-0000-0001-000000000001")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(1)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000001")));

    // onlyMyEvents
    mockMvc.perform(
        get("/events?limit=3&offset=0&sortBy=firstSessionStart&sortDirection=asc&onlyMyEvents=true")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(3))) //
        .andExpect(jsonPath("$.rows", hasSize(3)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000003")));

    // UPCOMING
    mockMvc.perform(get("/events?status=UPCOMING").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(2)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000001")));

    // PAST
    mockMvc.perform(get("/events?status=PAST").header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(3))) //
        .andExpect(jsonPath("$.rows", hasSize(3)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000003")));

    // All filters
    mockMvc
        .perform(get("/events?limit=3&offset=0&sortBy=firstSessionStart&sortDirection=asc&"
            + "topicId=00000000-0000-0000-0001-000000000001&onlyMyEvents=true&status=UPCOMING")
                .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.count", equalTo(2))) //
        .andExpect(jsonPath("$.rows", hasSize(2)))
        .andExpect(jsonPath("$.rows[0].id", equalTo("00000000-0000-0000-0002-000000000001")));
  }

  /**
   * Negative tests for search() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void search_40x() throws Exception {

    // Invalid paging
    mockMvc.perform(get("/events?limit=0&offset=-1").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid sortBy
    mockMvc.perform(get("/events?sortBy=invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid sortDirection
    mockMvc
        .perform(
            get("/events?sortDirection=invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid topicId
    mockMvc.perform(get("/events?topicId=invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid onlyMyEvents
    mockMvc
        .perform(get("/events?onlyMyEvents=invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Invalid status
    mockMvc.perform(get("/events?status=invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/events?status=invalid")).andExpect(status().is(401));
  }

  /**
   * Positive tests for get() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void get_200() throws Exception {
    mockMvc
        .perform(get("/events/00000000-0000-0000-0002-000000000002").header("Authorization",
            super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id", equalTo("00000000-0000-0000-0002-000000000002")))
        .andExpect(jsonPath("$.title", equalTo("title 2")))
        .andExpect(jsonPath("$.description", equalTo("description 2")))
        .andExpect(jsonPath("$.pointsForRegistering", equalTo(200)))
        .andExpect(jsonPath("$.pointsForScanningTicket", equalTo(201)))
        .andExpect(jsonPath("$.topicId", equalTo("00000000-0000-0000-0001-000000000001")))
        .andExpect(jsonPath("$.location", equalTo("location 2")))
        .andExpect(jsonPath("$.currentBooking", equalTo(20)))
        .andExpect(jsonPath("$.maxCapacity", equalTo(40)))
        .andExpect(jsonPath("$.waitingCapacity", equalTo(20)))
        .andExpect(jsonPath("$.tags[0]", equalTo("tag 3")))
        .andExpect(jsonPath("$.tags[1]", equalTo("tag 4")))
        .andExpect(jsonPath("$.portraitImageUrl", equalTo("http://doppler.com/portrait2.jpg")))
        .andExpect(jsonPath("$.landscapeImageUrl", equalTo("http://doppler.com/landscape2.jpg")))
        .andExpect(jsonPath("$.sessions", hasSize(2)))
        .andExpect(jsonPath("$.isMyEvent", equalTo(true)));
  }

  /**
   * Negative tests for get() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void get_40x() throws Exception {

    // Invalid id
    mockMvc.perform(get("/events/invalid").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000002"))
        .andExpect(status().is(401));

    // Not found
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000009").header("Authorization",
        super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for share() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void share_204() throws Exception {
    // Clear notifications
    notificationRepository.deleteAll();

    List<UserIdRequest> userIdRequests = new ArrayList<>();

    UserIdRequest userIdRequest = new UserIdRequest();
    userIdRequest.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    userIdRequests.add(userIdRequest);

    userIdRequest = new UserIdRequest();
    userIdRequest.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
    userIdRequests.add(userIdRequest);

    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/share")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userIdRequests))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(204));

    // Check notifications
    List<Notification> notifications = notificationRepository.findAll();
    assertEquals(2, notifications.size());
    assertEquals("full name shared you an event: title 1", notifications.get(0).getContent());
    assertEquals("http://example.com/photo.jpg", notifications.get(0).getIconUrl());
    assertEquals(false, notifications.get(0).isRead());
    assertEquals("00000000-0000-0000-0002-000000000001",
        notifications.get(0).getRelatedObjectId().toString());
    assertEquals("Event", notifications.get(0).getRelatedObjectType());
    assertEquals("00000000-0000-0000-0000-000000000004",
        notifications.get(0).getUserId().toString());
  }

  /**
   * Negative tests for share() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void share_40x() throws Exception {
    List<UserIdRequest> userIdRequests = new ArrayList<>();

    UserIdRequest userIdRequest = new UserIdRequest();
    userIdRequest.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    userIdRequests.add(userIdRequest);

    userIdRequest = new UserIdRequest();
    userIdRequest.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
    userIdRequests.add(userIdRequest);

    // Invalid event id
    mockMvc.perform(post("/events/invalid/share").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userIdRequests))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Empty array
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/share")
        .contentType(MediaType.APPLICATION_JSON).content("[]")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Null array item
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/share")
        .contentType(MediaType.APPLICATION_JSON).content("[null]")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Null userId
    userIdRequest.setUserId(null);
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/share")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userIdRequests))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));
    userIdRequest.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000005"));

    // Unauthorized
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/share")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userIdRequests))).andExpect(status().is(401));

    // Not found
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000009/share")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userIdRequests))
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }
}
