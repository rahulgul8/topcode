package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.UserEvent;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;

/**
 * Tests for UserEventController.
 */
public class UserEventControllerTest extends BaseControllerTest {

  /**
   * The user event repository.
   */
  @Autowired
  private UserEventRepository userEventRepository;

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * Positive tests for register() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  @Transactional
  public void register_200() throws Exception {
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000004/register")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(jsonPath("$.description", equalTo("You registered to an event: title 4")))
        .andExpect(jsonPath("$.points", equalTo(400)))
        .andExpect(jsonPath("$.userId", equalTo("00000000-0000-0000-0000-000000000001")))
        .andExpect(jsonPath("$.createdAt").exists());

    assertEquals(100 + 400,
        userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000001")).getPoints());
    assertNotNull(userEventRepository.findByUserIdAndEventId(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0002-000000000004")));
  }

  /**
   * Negative tests for register() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void register_40x() throws Exception {
    // Invalid event id
    mockMvc.perform(post("/events/invalid/register").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Already registered
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/register")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/register"))
        .andExpect(status().is(401));

    // Not found
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000009/register")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for getAllAttendees() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllAttendees_200() throws Exception {
    mockMvc
        .perform(get("/events/00000000-0000-0000-0002-000000000001/attendees")
            .header("Authorization", super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(3))) //
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0000-000000000001"))) //
        .andExpect(jsonPath("$[0].email", equalTo("user1@doppler.com"))) //
        .andExpect(jsonPath("$[0].fullName", equalTo("full name"))) //
        .andExpect(jsonPath("$[0].division", equalTo("division"))) //
        .andExpect(jsonPath("$[0].position", equalTo("position"))) //
        .andExpect(jsonPath("$[0].location", equalTo("location"))) //
        .andExpect(jsonPath("$[0].phoneNumber", equalTo("12345678"))) //
        .andExpect(jsonPath("$[0].photoUrl", equalTo("http://example.com/photo.jpg"))) //
        .andExpect(jsonPath("$[0].isNew", equalTo(false)));
  }

  /**
   * Negative tests for getAllAttendees() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getAllAttendees_40x() throws Exception {

    // Invalid event id
    mockMvc.perform(get("/events/invalid/attendees").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000001/attendees"))
        .andExpect(status().is(401));

    // Not found
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000009/attendees")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for scanTicket() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void scanTicket_200() throws Exception {
    UserIdRequest request = new UserIdRequest();
    request.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000002/scanTicket")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", "Bearer m2mSecret"))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$.id").exists()) //
        .andExpect(jsonPath("$.description", equalTo("You scanned ticket for an event: title 2"))) //
        .andExpect(jsonPath("$.points", equalTo(201))) //
        .andExpect(jsonPath("$.createdAt").exists()) //
        .andExpect(jsonPath("$.userId", equalTo("00000000-0000-0000-0000-000000000001")));

    UserEvent userEvent = userEventRepository.findByUserIdAndEventId(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0002-000000000002"));
    assertTrue(userEvent.isTicketScanned());
  }

  /**
   * Negative tests for scanTicket() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void scanTicket_40x() throws Exception {
    UserIdRequest request = new UserIdRequest();
    request.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    // Invalid event id
    mockMvc.perform(post("/events/invalid/scanTicket").contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer m2mSecret")
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(400));

    // Not registered
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000005/scanTicket")
        .header("Authorization", "Bearer m2mSecret").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(400));

    // Already scanned
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/scanTicket")
        .header("Authorization", "Bearer m2mSecret").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(400));

    // Non-existed userId
    request.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/scanTicket")
        .header("Authorization", "Bearer m2mSecret").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(400));

    // Unauthorized
    request.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/scanTicket")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(401));

    // Access denied
    request.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/scanTicket")
        .header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(403));

    // Non-existed event
    mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000009/scanTicket")
        .header("Authorization", "Bearer m2mSecret").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().is(404));
  }
}
