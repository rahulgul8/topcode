package com.doppler.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.User;
import com.doppler.entities.requests.UserSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.services.UserService;

/**
 * The controller defines user related endpoints.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  /**
   * The user service.
   */
  @Autowired
  private UserService service;

  /**
   * Search users.
   *
   * @param criteria the search criteria
   * @return the search result
   */
  @GetMapping
  public SearchResponse<User> search(@Valid @ModelAttribute UserSearchRequest criteria) {
    return service.search(criteria);
  }
}
