package com.doppler.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doppler.entities.SurveyAnswer;
import com.doppler.entities.SurveyQuestion;
import com.doppler.entities.UserEvent;
import com.doppler.entities.UserRewardPoint;
import com.doppler.repositories.SurveyAnswerRepository;
import com.doppler.repositories.SurveyQuestionRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.UserRewardPointDescriptionConfiguration;
import com.doppler.util.ApiConstants;

/**
 * The service provides survey related operations.
 */
@Service
@Transactional
public class SurveyService extends BaseService {
	
	public static final String SURVEYS_URI = "/surveys";
	
	public static final String RETRIEVEQUESTIONS_URI = "/questions";
	
	public static final String RESPONSE_URI = "/responses";

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
  
  @Autowired
  private BackendAPIService restApiService;

  /**
   * Get all survey questions of an event.
   * 
   * @param eventId the event id
   * @return the survey questions
   */
	@Transactional(readOnly = false)
	public List<SurveyQuestion> getSurveyQuestions(UUID eventId) {
		List<SurveyQuestion> surveyQuestion = restApiService.getForList(SurveyQuestion.class,
				ApiConstants.BASE_URI + SURVEYS_URI + "/{eventId}" + RETRIEVEQUESTIONS_URI, eventId);
		return surveyQuestion;
	}

  /**
   * Submit survey answers for an event.
   * 
   * @param eventId the event id
   * @param surveyAnswers the survey answers
   * @return the user reward point
   */
  public UserRewardPoint submitSurveyAnswers(UUID eventId, List<SurveyAnswer> surveyAnswers) {
	  UserRewardPoint userRewardPoint = restApiService.postForEntity(UserRewardPoint.class, ApiConstants.BASE_URI +SURVEYS_URI+ "/{eventId}" + RESPONSE_URI, surveyAnswers,
			  eventId);
	  return userRewardPoint;
  }
}
