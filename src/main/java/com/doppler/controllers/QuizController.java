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
import com.doppler.entities.QuizAnswer;
import com.doppler.entities.QuizQuestion;
import com.doppler.entities.UserRewardPoint;
import com.doppler.services.QuizService;

/**
 * The controller defines quiz related endpoints.
 */
@RestController
@RequestMapping("/events/{eventId}/quiz")
public class QuizController {

  /**
   * The quiz service.
   */
  @Autowired
  private QuizService service;

  /**
   * Get all quiz questions of an event.
   * 
   * @param eventId the event id
   * @return the quiz questions
   */
  @GetMapping
  public List<QuizQuestion> getQuizQuestions(@PathVariable("eventId") UUID eventId) {
    return service.getQuizQuestions(eventId);
  }

  /**
   * Submit quiz answers for an event.
   * 
   * @param eventId the event id
   * @param quizAnswers the quiz answers
   * @return the user reward point
   */
  @PostMapping
  public UserRewardPoint submitQuizAnswers(@PathVariable("eventId") UUID eventId,
      @Valid @RequestBody List<QuizAnswer> quizAnswers) {
    return service.submitQuizAnswers(eventId, quizAnswers);
  }
}
