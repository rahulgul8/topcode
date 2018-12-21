package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.QuizQuestion;

/**
 * The repository provides operations on Quiz Question entity.
 */
@Repository
public interface QuizQuestionRepository extends BaseRepository<QuizQuestion> {

  /**
   * Find the quiz questions of a specified event.
   * 
   * @param eventId the event id
   * @return the quiz questions
   */
  List<QuizQuestion> findByEventId(UUID eventId);
}
