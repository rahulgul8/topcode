package com.doppler.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Topic;
import com.doppler.repositories.TopicRepository;

/**
 * The service provides topic related operations.
 */
@Service
@Transactional
public class TopicService extends BaseService {

  /**
   * The topic repository.
   */
  @Autowired
  private TopicRepository topicRepository;

  /**
   * Get all topics.
   * 
   * @return the topics
   */
  @Transactional(readOnly = true)
  public List<Topic> search() {
    return topicRepository.findAll(Sort.by("name"));
  }
}
