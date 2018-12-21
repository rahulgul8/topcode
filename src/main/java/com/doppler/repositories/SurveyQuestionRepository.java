package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.SurveyQuestion;

/**
 * The repository provides operations on Survey Question entity.
 */
@Repository
public interface SurveyQuestionRepository extends BaseRepository<SurveyQuestion> {

  /**
   * Find the survey questions of a specified event.
   * 
   * @param eventId the event id
   * @return the survey questions
   */
  List<SurveyQuestion> findByEventId(UUID eventId);
}
