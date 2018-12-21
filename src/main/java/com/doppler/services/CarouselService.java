package com.doppler.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Carousel;
import com.doppler.repositories.CarouselRepository;

/**
 * The service provides carousel related operations.
 */
@Service
@Transactional
public class CarouselService extends BaseService {

  /**
   * The carousel repository.
   */
  @Autowired
  private CarouselRepository carouselRepository;

  /**
   * Get all carousels.
   * 
   * @return the carousels
   */
  @Transactional(readOnly = true)
  public List<Carousel> search() {
    return carouselRepository.findAll();
  }
}
