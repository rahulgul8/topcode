package com.doppler.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.UserSetting;
import com.doppler.services.UserService;

/**
 * The controller defines user setting related endpoints.
 */
@RestController
@RequestMapping("/userSettings")
public class UserSettingController {

  /**
   * The user service.
   */
  @Autowired
  private UserService service;

  /**
   * Get user setting.
   * 
   * @return the user setting
   */
  @GetMapping
  public UserSetting getSetting() {
    return service.getSetting();
  }

  /**
   * Update user setting.
   * 
   * @param userSetting the user setting
   * @return the updated user setting
   */
  @PostMapping
  public UserSetting updateSetting(@Valid @RequestBody UserSetting userSetting) {
    return service.updateSetting(userSetting);
  }
}
