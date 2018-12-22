package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import com.doppler.entities.SurveyAnswerOption;
import com.doppler.entities.SurveyQuestion;
import com.doppler.entities.UserRewardPoint;
import com.doppler.services.SurveyService;

/**
 * Tests for SurveyController.
 */
public class SurveyControllerTest extends BaseControllerTest {

	@MockBean
	SurveyService surveyService;

	/**
	 * Positive tests for getSurveyQuestions() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void getSurveyQuestions_200() throws Exception {

		List<SurveyQuestion> surveyQuestions = new ArrayList<>();
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		SurveyAnswerOption answerOption = new SurveyAnswerOption();
		answerOption.setAnswerOption("option 1");
		answerOption.setId(UUID.fromString("00000000-0000-0000-0005-000000000001"));
		List<SurveyAnswerOption> surveyAnswerOptions = new ArrayList<>();
		surveyAnswerOptions.add(answerOption);
		surveyQuestion.setSurveyAnswerOptions(surveyAnswerOptions);
		surveyQuestion.setId(UUID.fromString("00000000-0000-0000-0004-000000000001"));
		surveyQuestion.setQuestion("question 1");
		surveyQuestion.setType("multipleChoice");
		surveyQuestions.add(surveyQuestion);

		Mockito.when(surveyService.getSurveyQuestions(Mockito.any())).thenReturn(surveyQuestions);
		mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000001/survey").header("Authorization",
				super.userBearerToken)).andExpect(status().is(200)) //
				.andExpect(jsonPath("$", hasSize(1))) //
				.andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0004-000000000001")))
				.andExpect(jsonPath("$[0].question", equalTo("question 1")))
				.andExpect(jsonPath("$[0].type", equalTo("multipleChoice")))
				.andExpect(jsonPath("$[0].surveyAnswerOptions[0].id", equalTo("00000000-0000-0000-0005-000000000001")))
				.andExpect(jsonPath("$[0].surveyAnswerOptions[0].answerOption", equalTo("option 1")));
	}

	/**
	 * Negative tests for getSurveyQuestions() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void getSurveyQuestions_40x() throws Exception {

		// Invalid id
		mockMvc.perform(get("/events/invalid/survey").header("Authorization", super.userBearerToken))
				.andExpect(status().is(400));

		// Unauthorized
		mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000001/survey")).andExpect(status().is(401));

		// Not attended yet
		Mockito.when(surveyService.getSurveyQuestions(Mockito.any()))
				.thenThrow(new AccessDeniedException("Access denied"));
		mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000003/survey").header("Authorization",
				super.userBearerToken)).andExpect(status().is(403));

		Mockito.reset(surveyService);

		// Not found
		Mockito.when(surveyService.getSurveyQuestions(Mockito.any())).thenThrow(new EntityNotFoundException());
		mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000009/survey").header("Authorization",
				super.userBearerToken)).andExpect(status().is(404));
	}

	/**
	 * Positive tests for submitSurveyAnswers() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void submitSurveyAnswers_200() throws Exception {
		String body = "[" + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000001\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000001\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000001\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000011\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000002\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000002\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000003\"," + //
				"        \"selectedSurveyAnswerOptionId\": null," + //
				"        \"textAnswer\": \"a free text\"" + //
				"    }" + //
				"]";

		UserRewardPoint userRewardPoint = new UserRewardPoint();
		userRewardPoint.setDescription("You submitted survey for an event: title 1");
		userRewardPoint.setPoints(102);
		userRewardPoint.setCreatedAt(new Date());
		userRewardPoint.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		userRewardPoint.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
		Mockito.when(surveyService.submitSurveyAnswers(Mockito.any(), Mockito.any())).thenReturn(userRewardPoint);
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
				.header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON).content(body)) //
				.andExpect(status().is(200)) //
				.andExpect(jsonPath("$.id").exists()) //
				.andExpect(jsonPath("$.description", equalTo("You submitted survey for an event: title 1"))) //
				.andExpect(jsonPath("$.points", equalTo(102))) //
				.andExpect(jsonPath("$.createdAt").exists()) //
				.andExpect(jsonPath("$.userId", equalTo("00000000-0000-0000-0000-000000000001")));
	}

	/**
	 * Negative tests for submitSurveyAnswers() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void submitSurveyAnswers_40x() throws Exception {

		String body = "[" + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000001\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000001\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000001\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000011\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000002\"," + //
				"        \"selectedSurveyAnswerOptionId\": \"00000000-0000-0000-0005-000000000002\"," + //
				"        \"textAnswer\": null" + //
				"    }," + //
				"    {" + //
				"        \"surveyQuestionId\": \"00000000-0000-0000-0004-000000000003\"," + //
				"        \"selectedSurveyAnswerOptionId\": null," + //
				"        \"textAnswer\": \"a free text\"" + //
				"    }" + //
				"]";

		// Mockito.when(surveyService.submitSurveyAnswers(Mockito.any(),
		// Mockito.any())).thenReturn(userRewardPoint);
		// Invalid id
		mockMvc.perform(post("/events/invalid/survey").header("Authorization", super.userBearerToken)
				.contentType(MediaType.APPLICATION_JSON).content(body)) //
				.andExpect(status().is(400));

		Mockito.when(surveyService.submitSurveyAnswers(Mockito.any(), Mockito.any()))
				.thenThrow(new IllegalArgumentException("exception occurred"));
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
				.header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON).content("[]")) //
				.andExpect(status().is(400));

		// Null answer
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
				.header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
				.content("[ null ]")) //
				.andExpect(status().is(400));

		// Unauthorized
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
				.contentType(MediaType.APPLICATION_JSON).content(body)) // .contentType(MediaType.APPLICATION_JSON)
				.andExpect(status().is(401));

		// Not attended
		Mockito.reset(surveyService);
		Mockito.when(surveyService.submitSurveyAnswers(Mockito.any(), Mockito.any()))
				.thenThrow(new AccessDeniedException("invalid token"));

		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000003/survey")
				.header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(body)) //
				.andExpect(status().is(403));

		// Already submitted
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
				.header("Authorization", super.user2BearerToken).contentType(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(body)) //
				.andExpect(status().is(403));

		Mockito.reset(surveyService);
		Mockito.when(surveyService.submitSurveyAnswers(Mockito.any(), Mockito.any()))
				.thenThrow(new EntityNotFoundException());
		// Not found
		mockMvc.perform(post("/events/00000000-0000-0000-0002-000000000009/survey")
				.header("Authorization", super.user2BearerToken).contentType(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(body)) //
				.andExpect(status().is(404));
	}
}
