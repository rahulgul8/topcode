package com.doppler.services;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Event;
import com.doppler.entities.QuizAnswer;
import com.doppler.entities.QuizQuestion;
import com.doppler.entities.User;
import com.doppler.entities.UserEvent;
import com.doppler.entities.UserRewardPoint;
import com.doppler.repositories.QuizAnswerRepository;
import com.doppler.repositories.QuizQuestionRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.UserRewardPointDescriptionConfiguration;

/**
 * The service provides quiz related operations.
 */
@Service
@Transactional
public class QuizService extends BaseService {

  /**
   * The quiz question repository.
   */
  @Autowired
  private QuizQuestionRepository quizQuestionRepository;

  /**
   * The user event repository.
   */
  @Autowired
  private UserEventRepository userEventRepository;

  /**
   * The quiz answer repository.
   */
  @Autowired
  private QuizAnswerRepository quizAnswerRepository;

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
   * Get all quiz questions of an event.
   * 
   * @param eventId the event id
   * @return the quiz questions
   */
  @Transactional(readOnly = false)
  public List<QuizQuestion> getQuizQuestions(UUID eventId) {
    // Validate that the event exists
    getEventById(eventId);

    // Validate that the user has attended the event
    getUserEvent(eventId);

    return quizQuestionRepository.findByEventId(eventId);
  }

  /**
   * Submit quiz answers for an event.
   * 
   * @param eventId the event id
   * @param quizAnswers the quiz answers
   * @return the user reward point
   */
  public UserRewardPoint submitQuizAnswers(UUID eventId, List<QuizAnswer> quizAnswers) {

    // Validate answers
    validateList(quizAnswers, "quizAnswers");

    // Validate that the event exists
    Event event = getEventById(eventId);

    // Validate that the user has attended the event
    UserEvent userEvent = getUserEvent(eventId);

    // Validate that the user has not submitted the quiz
    if (userEvent.isSubmittedQuiz()) {
      throw new AccessDeniedException("You already submitted quiz for this event");
    }

    userEvent.setSubmittedQuiz(true);
    userEventRepository.save(userEvent);

    // Save answers
    User user = SecurityUtils.getCurrentUser();
    quizAnswers.forEach(x -> x.setUserId(user.getId()));
    quizAnswerRepository.saveAll(quizAnswers);

    // Add points
    user.setPoints(user.getPoints() + event.getPointsForSubmittingQuiz());
    userRepository.save(user);

    // Create UserRewardPoint
    UserRewardPoint userRewardPoint = new UserRewardPoint();
    userRewardPoint.setDescription(
        String.format(userRewardPointDescriptionConfiguration.getSubmitQuiz(), event.getTitle()));
    userRewardPoint.setPoints(event.getPointsForSubmittingQuiz());
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
      throw new AccessDeniedException("You must attend the event to access the quiz");
    }

    return userEvent;
  }
}
