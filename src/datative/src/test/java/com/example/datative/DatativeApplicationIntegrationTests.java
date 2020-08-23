package com.example.datative;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DatativeApplicationIntegrationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@Test //Checking the correct view is returned when registration is called
	public void givenHomePageUri_whenMockMVC_thenReturnsIndexView() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
	}
	
	@Test //Checking the correct view is returned when login is called
	public void givenLoginPageUri_whenMockMVC_thenReturnsLoginView() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}	
	
	@Test//Checking content of index view is correct
	public void testIndexViewContent_PassIfCorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Upload a new dataset to get started creating your custom dashboard and report")));
	}
	
	@Test//Checking content of Login view is correct
	public void testLoginViewContent_PassIfCorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/login"))
			    .andExpect(content().string(containsString("Login")));
	}
	
	@Test(expected = AssertionError.class)//Checking content of Login view is correct
	public void testIndexViewContent_PassIfIncorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/"))
			    .andExpect(content().string(containsString("This is not on this page")));
	}
	
	@Test(expected = AssertionError.class)//Checking content of Login view is correct
	public void testLoginViewContent_PassIfIncorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/login"))
			    .andExpect(content().string(containsString("This is not on this page")));
	}
	
	
}

