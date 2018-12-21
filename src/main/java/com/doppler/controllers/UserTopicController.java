package com.doppler.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.UserRewardPoint;
import com.doppler.entities.UserTopic;
import com.doppler.entities.requests.UserTopicRequest;
import com.doppler.services.UserTopicService;

/**
 * The controller defines user topic related endpoints.
 */
@RestController
@RequestMapping("/userTopics")
public class UserTopicController {

  /**
   * The user topic service.
   */
  @Autowired
  private UserTopicService service;

  /**
   * Get all user topics.
   * 
   * @return the user topics
   */
  @GetMapping
  public List<UserTopic> search() {
    return service.search();
  }

  /**
   * Update user topics.
   * 
   * @param request the request
   * @return the user reward point if this is a new user, otherwise null
   */
  @PostMapping
  public UserRewardPoint update(@Valid @RequestBody List<UserTopicRequest> request) {
    return service.update(request);
  }
}
