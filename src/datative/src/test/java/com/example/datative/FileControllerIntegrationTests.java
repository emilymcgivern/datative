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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileControllerIntegrationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	
	MockMultipartFile multipartEmptyFile = new MockMultipartFile("test.csv", "test.csv", "text/csv", "".getBytes());
	MockMultipartFile multipartCsvFile = new MockMultipartFile("test.csv", "test.csv", "text/csv", "My CSV".getBytes());
	String[] tableName = {"test"};
	
	@Test
	public void givenUploadPageUri_whenMockMVC_thenReturnsViewName() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/upload")).andExpect(status().isOk()).andExpect(view().name("upload"));
	}

	
	@Test //Check that redirect occur when file is empty
	public void shouldHaveStatus302ForRedirect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadFile")
                .file("file", multipartEmptyFile.getBytes())
                .characterEncoding("UTF-8"))
        		.andExpect(status().is(302));
	}
	
	@Test //Checks that redirect occur when table name exists
	@WithUserDetails("testeraccount@mail.com")
	public void shouldHaveStatus302ForRedirect_TableNameExists() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadFile")
                .file("file", multipartCsvFile.getBytes())
                .param("tableName", tableName)
                .characterEncoding("UTF-8"))
			    .andExpect(status().is(302));
	}
	
	@Test
	public void testUploadViewContent_PassIfCorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/upload"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Upload a new file to begin creating your new dashboard.")));
	}
	
	@Test(expected = AssertionError.class) 
	public void testUploadViewContent_PassIfIncorrect() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("/upload"))
			    .andExpect(content().string(containsString("This string is not on the page")));
	}
	
	
}
