package com.example.datative;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.datative.dashboard.DashboardController;
import com.example.datative.dashboard.DashboardService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DashboardTests {
	
	@Autowired 
	DashboardController dashboardController;
	
	@Autowired
	DashboardService dashboardService;
	
	
	@SuppressWarnings("serial")
	@Test
	public void checkCorrectDatasetReturnedCsv_PassIfEqual() {		
		List<Map<String, String>> result = dashboardService.getDataSet("test_csv341");
		Map<String, String> params = new HashMap<String, String>() {
			{
				put("No. of confirmed cases", "IntegerType");
				put("No. of suspected deaths", "IntegerType");
				put("No. of probable cases", "IntegerType");
				put("No. of confirmed deaths", "IntegerType");
				put("No. of suspected cases", "IntegerType");
				put("Country", "StringType");
				put("No. of confirmed, probable and suspected deaths", "IntegerType");
				put("No. of confirmed, probable and suspected cases", "IntegerType");
				put("Date", "DateType");
				put("No. of probable deaths", "IntegerType");	
			}
		};
		
		List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
		
		expectedResult.add(params);
		assertEquals(result, expectedResult);
		
	}
	
	@SuppressWarnings("serial")
	@Test
	public void checkCorrectDatasetReturnedTxt_PassIfEqual() {		
		List<Map<String, String>> result = dashboardService.getDataSet("test_txt341");
		Map<String, String> params = new HashMap<String, String>() {
			{
				put("No. of confirmed cases", "IntegerType");
				put("No. of suspected deaths", "IntegerType");
				put("No. of probable cases", "IntegerType");
				put("No. of confirmed deaths", "IntegerType");
				put("No. of suspected cases", "IntegerType");
				put("Country", "StringType");
				put("No. of confirmed, probable and suspected deaths", "IntegerType");
				put("No. of confirmed, probable and suspected cases", "IntegerType");
				put("Date", "DateType");
				put("No. of probable deaths", "IntegerType");	
			}
		};
		
		List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
		
		expectedResult.add(params);
		assertEquals(result, expectedResult);
		
	}
	
	@Test(expected = SQLSyntaxErrorException.class)
	public void checkIncorrectTableName_ReturnSQLSyntaxErrorException() {
		dashboardService.getDataSet("notable");
	}
	
	@Test
	@WithUserDetails("testeraccount@mail.com")
	public void getUserBucketString_PassIfEqual() {
		String result = dashboardService.getUserBucket();
		String expectedResult = "341testeraccountpdf";
		assertEquals(result, expectedResult);
	}
	
	@Test
	@WithUserDetails("testeraccount@mail.com")
	public void getUserBucketString_PassIfNotEqual() {
		String result = dashboardService.getUserBucket();
		String expectedResult = "0000testeraccount";
		assertThat(result, not(expectedResult));
	}
	


}
