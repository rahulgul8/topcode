package com.doppler.services;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Event;
import com.doppler.entities.SurveyAnswer;
import com.doppler.entities.SurveyQuestion;
import com.doppler.entities.User;
import com.doppler.entities.UserEvent;
import com.doppler.entities.UserRewardPoint;
import com.doppler.repositories.SurveyAnswerRepository;
import com.doppler.repositories.SurveyQuestionRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.UserRewardPointDescriptionConfiguration;

/**
 * The service provides survey related operations.
 */
@Service
@Transactional
public class SurveyService extends BaseService {

  /**
   * The survey question repository.
   */
  @Autowired
  private SurveyQuestionRepository surveyQuestionRepository;

  /**
   * The user event repository.
   */
  @Autowired
  private UserEventRepository userEventRepository;

  /**
   * The survey answer repository.
   */
  @Autowired
  private SurveyAnswerRepository surveyAnswerRepository;

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * The user reward point repository.
   */
  @Autowired
  private UserRewardPointRepository userRewardPointRepository;

  /**
   * The user reward point description configuration.
   */
  @Autowired
  private UserRewardPointDescriptionConfiguration userRewardPointDescriptionConfiguration;

  /**
   * Get all survey questions of an event.
   * 
   * @param eventId the event id
   * @return the survey questions
   */
  @Transactional(readOnly = false)
  public List<SurveyQuestion> getSurveyQuestions(UUID eventId) {
    // Validate that the event exists
    getEventById(eventId);

    // Validate that the user has attended the event
    getUserEvent(eventId);

    return surveyQuestionRepository.findByEventId(eventId);
  }

  /**
   * Submit survey answers for an event.
   * 
   * @param eventId the event id
   * @param surveyAnswers the survey answers
   * @return the user reward point
   */
  public UserRewardPoint submitSurveyAnswers(UUID eventId, List<SurveyAnswer> surveyAnswers) {

    // Validate answers
    validateList(surveyAnswers, "surveyAnswers");

    // Validate that the event exists
    Event event = getEventById(eventId);

    // Validate that the user has attended the event
    UserEvent userEvent = getUserEvent(eventId);

    // Validate that the user has not submitted the survey
    if (userEvent.isSubmittedSurvey()) {
      throw new AccessDeniedException("You already submitted survey for this event");
    }

    userEvent.setSubmittedSurvey(true);
    userEventRepository.save(userEvent);

    // Save answers
    User user = SecurityUtils.getCurrentUser();
    surveyAnswers.forEach(x -> x.setUserId(user.getId()));
    surveyAnswerRepository.saveAll(surveyAnswers);

    // Add points
    user.setPoints(user.getPoints() + event.getPointsForSubmittingSurvey());
    userRepository.save(user);

    // Create UserRewardPoint
    UserRewardPoint userRewardPoint = new UserRewardPoint();
    userRewardPoint.setDescription(
        String.format(userRewardPointDescriptionConfiguration.getSubmitSurvey(), event.getTitle()));
    userRewardPoint.setPoints(event.getPointsForSubmittingSurvey());
    userRewardPoint.setUserId(user.getId());

    return userRewardPointRepository.save(userRewardPoint);
  }

  /**
   * Get the user event.
   * 
   * @param eventId the event id
   * @return the user event
   */
  private UserEvent getUserEvent(UUID eventId) {
    UserEvent userEvent =
        userEventRepository.findByUserIdAndEventId(SecurityUtils.getCurrentUser().getId(), eventId);
    if (userEvent == null || !userEvent.isTicketScanned()) {
      throw new AccessDeniedException("You must attend the event to access the survey");
    }

    return userEvent;
  }
}
