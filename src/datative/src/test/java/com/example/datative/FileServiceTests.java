package com.example.datative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import com.example.datative.file.FileService;
import com.example.datative.security.model.User;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileServiceTests {
	
	@Autowired
	FileService fileService;
	
	@Test //Testing that table name that already exists returns true
	@WithUserDetails("testeraccount@mail.com")
	public void testTableNameExists_AssertTrueIfExists() throws SQLException {
		ResultSet results = fileService.checkForTable("test");
		assertTrue(results.next());		
	}
	
	@Test //Testing that table name that doesn't already exists returns false
	@WithUserDetails("testeraccount@mail.com")
	public void testTableNameDoesNotExist_AssertFalseIfDoesNotExist() throws SQLException {
		ResultSet results = fileService.checkForTable("myuniquetablename");
		assertFalse(results.next());		
	}
	
	@Test //Checking authenticated user first name correctly returned
	@WithUserDetails("testeraccount@mail.com")
	public void getCorrectAuthenticatedUserDetails_PassIfFirstNameEqual() {
		User result = fileService.getAuthenticatedUser();
		String userFirstName = result.getFirstName();
		assertEquals(userFirstName, "Tester");		
	}
	
	@Test //Checking authenticated user email correctly returned
	@WithUserDetails("testeraccount@mail.com")
	public void getCorrectAuthenticatedUserDetails_PassIfEmailEqual() {
		User result = fileService.getAuthenticatedUser();
		String userFirstName = result.getEmail();
		assertEquals(userFirstName, "testeraccount@mail.com");		
	}
	
	@Test //Testing that csv file is correctly processed in file service
	@WithUserDetails("testeraccount@mail.com")
	public void uploadNewCsvFile_PassIfTableNameEqual() throws IOException {
        File resFile = ResourceUtils.getFile("classpath:datacleaning-test.csv");
        InputStream in = new FileInputStream(resFile);
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", in);
		String tableName = fileService.uploadFile(file, "mytesttable");
		assertEquals(tableName, "mytesttable341");		
	}
	
    @AfterClass //Removing table and record created during test
    public static void tearDown() throws SQLException {
    	Connection con = FileService.createDbConnection();
		Statement statement = con.createStatement();
		statement.addBatch("drop table mytesttable341");
	    statement.addBatch("DELETE FROM user_file WHERE table_name = 'mytesttable341';");
	    statement.executeBatch();
    }
	
	
}
