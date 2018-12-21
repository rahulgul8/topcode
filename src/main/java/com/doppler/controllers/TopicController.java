package com.doppler.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Topic;
import com.doppler.services.TopicService;

/**
 * The controller defines topic related endpoints.
 */
@RestController
@RequestMapping("/topics")
public class TopicController {

  /**
   * The topic service.
   */
  @Autowired
  private TopicService service;

  /**
   * Get all topics.
   * 
   * @return the topics
   */
  @GetMapping
  public List<Topic> search() {
    return service.search();
  }
}
