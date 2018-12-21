package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * Tests for SurveyController.
 */
public class SurveyControllerTest extends BaseControllerTest {

  /**
   * Positive tests for getSurveyQuestions() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getSurveyQuestions_200() throws Exception {
    mockMvc
        .perform(get("/events/00000000-0000-0000-0002-000000000001/survey").header("Authorization",
            super.userBearerToken))
        .andExpect(status().is(200)) //
        .andExpect(jsonPath("$", hasSize(3))) //
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0004-000000000001")))
        .andExpect(jsonPath("$[0].question", equalTo("question 1")))
        .andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0004-000000000001")))
        .andExpect(jsonPath("$[0].type", equalTo("multipleChoice")))
        .andExpect(jsonPath("$[0].surveyAnswerOptions[0].id",
            equalTo("00000000-0000-0000-0005-000000000001")))
        .andExpect(jsonPath("$[0].surveyAnswerOptions[0].answerOption", equalTo("option 1")));
  }

  /**
   * Negative tests for getSurveyQuestions() method.
   * 
   * @throws Exception if any error occurs
   */
  @Test
  public void getSurveyQuestions_40x() throws Exception {

    // Invalid id
    mockMvc.perform(get("/events/invalid/survey").header("Authorization", super.userBearerToken))
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000001/survey"))
        .andExpect(status().is(401));

    // Not attended yet
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000003/survey")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(403));

    // Not found
    mockMvc.perform(get("/events/00000000-0000-0000-0002-000000000009/survey")
        .header("Authorization", super.userBearerToken)).andExpect(status().is(404));
  }

  /**
   * Positive tests for submitSurveyAnswers() method.
   * 
   * @throws Exception if any error occurs
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

    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
            .header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
            .content(body)) //
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
   * @throws Exception if any error occurs
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

    // Invalid id
    mockMvc
        .perform(post("/events/invalid/survey").header("Authorization", super.userBearerToken)
            .contentType(MediaType.APPLICATION_JSON).content(body)) //
        .andExpect(status().is(400));

    // No answer
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
            .header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
            .content("[]")) //
        .andExpect(status().is(400));

    // Null answer
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
            .header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
            .content("[ null ]")) //
        .andExpect(status().is(400));

    // Unauthorized
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
            .contentType(MediaType.APPLICATION_JSON).content(body)) // .contentType(MediaType.APPLICATION_JSON)
        .andExpect(status().is(401));

    // Not attended
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000003/survey")
            .header("Authorization", super.userBearerToken).contentType(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).content(body)) //
        .andExpect(status().is(403));

    // Already submitted
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000001/survey")
            .header("Authorization", super.user2BearerToken).contentType(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).content(body)) //
        .andExpect(status().is(403));

    // Not found
    mockMvc
        .perform(post("/events/00000000-0000-0000-0002-000000000009/survey")
            .header("Authorization", super.user2BearerToken).contentType(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).content(body)) //
        .andExpect(status().is(404));
  }
}
