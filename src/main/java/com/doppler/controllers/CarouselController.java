package com.doppler.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Carousel;
import com.doppler.services.CarouselService;

/**
 * The controller defines carousel related endpoints.
 */
@RestController
@RequestMapping("/carousels")
public class CarouselController {

  /**
   * The carousel service.
   */
  @Autowired
  private CarouselService service;

  /**
   * Get all carousels.
   * 
   * @return the carousels
   */
  @GetMapping
  public List<Carousel> search() {
    return service.search();
  }
}
