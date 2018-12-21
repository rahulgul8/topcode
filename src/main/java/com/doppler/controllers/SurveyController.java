package com.doppler.controllers;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.SurveyAnswer;
import com.doppler.entities.SurveyQuestion;
import com.doppler.entities.UserRewardPoint;
import com.doppler.services.SurveyService;

/**
 * The controller defines survey related endpoints.
 */
@RestController
@RequestMapping("/events/{eventId}/survey")
public class SurveyController {

  /**
   * The survey service.
   */
  @Autowired
  private SurveyService service;

  /**
   * Get all survey questions of an event.
   * 
   * @param eventId the event id
   * @return the survey questions
   */
  @GetMapping
  public List<SurveyQuestion> getSurveyQuestions(@PathVariable("eventId") UUID eventId) {
    return service.getSurveyQuestions(eventId);
  }

  /**
   * Submit survey answers for an event.
   * 
   * @param eventId the event id
   * @param surveyAnswers the survey answers
   * @return the user reward point
   */
  @PostMapping
  public UserRewardPoint submitSurveyAnswers(@PathVariable("eventId") UUID eventId,
      @Valid @RequestBody List<SurveyAnswer> surveyAnswers) {
    return service.submitSurveyAnswers(eventId, surveyAnswers);
  }
}
