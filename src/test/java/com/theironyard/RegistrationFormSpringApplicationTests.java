package com.theironyard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.User;
import com.theironyard.services.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RegistrationFormSpringApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // junit will run test run in order if they rely on each other
public class RegistrationFormSpringApplicationTests {

	@Autowired
	WebApplicationContext wac;

	MockMvc mockMvc;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Autowired
	UserRepository users;

	@Test
	public void aTestAddUser() throws Exception {
		User user = new User();
		user.setUsername("Erik");
		user.setAddress("123 st");
		user.setEmail("E@emial.com");

		ObjectMapper om = new ObjectMapper(); // object mapper is used to convert json to a string
		String json = om.writeValueAsString(user);

		//mockmvc is creating a post request
		mockMvc.perform(
				MockMvcRequestBuilders.post("/user")
						.content(json)
						.contentType("application/json") //let application now it is being parsed as json
		);

		Assert.assertTrue(users.count() == 1);
	}

	@Test
	public void bTestEdit() throws Exception {
		User user = users.findOne(1); // pulls user with id of 1
		user.setUsername("Bob");
		user.setAddress("321 st");
		user.setEmail("B@email.com");


		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(user);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user")
						.content(json)
						.contentType("application/json")
		);

		User editedUser = users.findOne(1);
		Assert.assertTrue(editedUser.getUsername().equals(user.getUsername()));

	}

	@Test
	public void cTestGet() throws Exception {
		ResultActions ra = mockMvc.perform(  // spring io result actions
				MockMvcRequestBuilders.get("/user")

		);
		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();
		ObjectMapper om = new ObjectMapper();
		ArrayList<HashMap<String, String>> userMaps = om.readValue(json, ArrayList.class);

		Assert.assertTrue(userMaps.size() == 1 && userMaps.get(0).get("username").equals("Bob"));
	}

	@Test
	public void dTestDeleteUser() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/user/1") //user / id = 1
		);

		Assert.assertTrue(users.count() == 0);
	}



}
