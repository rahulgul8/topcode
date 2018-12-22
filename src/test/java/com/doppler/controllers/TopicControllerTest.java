package com.doppler.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.doppler.entities.Topic;
import com.doppler.services.TopicService;

/**
 * Tests for TopicController.
 */
public class TopicControllerTest extends BaseControllerTest {

	@MockBean
	TopicService topicService;

	/**
	 * Positive tests for search() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void search_200() throws Exception {
		List<Topic> topics = new ArrayList<>();
		Topic topic = new Topic();
		topic.setId(UUID.fromString("00000000-0000-0000-0001-000000000001"));
		topic.setName("name 1");
		topic.setIconUrl("http://doppler.com/icon1.jpg");
		topics.add(topic);
		Mockito.when(topicService.retrieveTopics()).thenReturn(topics);
		mockMvc.perform(get("/topics").header("Authorization", super.userBearerToken)).andExpect(status().is(200)) //
				.andExpect(jsonPath("$", hasSize(1)))//
				.andExpect(jsonPath("$[0].id", equalTo("00000000-0000-0000-0001-000000000001")))
				.andExpect(jsonPath("$[0].name", equalTo("name 1")))
				.andExpect(jsonPath("$[0].iconUrl", equalTo("http://doppler.com/icon1.jpg")));
	}

	/**
	 * Negative tests for search() method.
	 * 
	 * @throws Exception
	 *             if any error occurs
	 */
	@Test
	public void search_40x() throws Exception {

		// Unauthorized
		mockMvc.perform(get("/topics")).andExpect(status().is(401));
	}
}
