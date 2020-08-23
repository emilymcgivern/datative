package com.example.datative;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.example.datative.components.Crosstab;
import com.example.datative.dashboard.DashboardService;

@SpringBootTest
public class CrosstabIntegrationTests {
	
	@Autowired
	Crosstab crossTab;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	
	@SuppressWarnings("serial")
	@Test
	public void checkCrosstabControllerParamsAndResultSum_PassIfStatus200AndExpectedResult() throws Exception {	
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.student_test341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable"); 
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(){
			{
			     put("opName", Collections.singletonList("sum"));
			     put("columnName", Collections.singletonList("gender"));
			     put("rowName", Collections.singletonList("university"));
			     put("aggregateName", Collections.singletonList("grade"));
			}
		};
		
		
		ArrayList<HashMap<String, Object>> expectedResult = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> obj1 = new HashMap<String, Object>(){
			{
			     put("gender", "Male");
			     put("university", "DCU");
			     put("value", 76);

			}
		};
		
		HashMap<String, Object> obj2 = new HashMap<String, Object>(){
			{
			     put("gender", "Female");
			     put("university", "DCU");
			     put("value", 63);

			}
		};
		
		HashMap<String, Object> obj3 = new HashMap<String, Object>(){
			{
			     put("gender", "Female");
			     put("university", "UCD");
			     put("value", 1);

			}
		};
		
		expectedResult.add(obj1);
		expectedResult.add(obj2);
		expectedResult.add(obj3);
		
		mockMvc.perform(post("/crosstabComp")
	            .params(params))
	    		.andExpect(status().isOk())
	    		.andExpect(jsonPath("$[0]", is(obj1)))
	            .andExpect(jsonPath("$[1]", is(obj2)))
	            .andExpect(jsonPath("$[2]", is(obj3)));
	}
	
	@SuppressWarnings("serial")
	@Test
	public void checkCrosstabControllerParamsAndResultCount_PassIfStatus200AndExpectedResult() throws Exception {	
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.student_test341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable"); 
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(){
			{
			     put("opName", Collections.singletonList("count"));
			     put("columnName", Collections.singletonList("gender"));
			     put("rowName", Collections.singletonList("university"));
			}
		};
		
		
		ArrayList<HashMap<String, Object>> expectedResult = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> obj1 = new HashMap<String, Object>(){
			{
			     put("gender", "Male");
			     put("university", "DCU");

			}
		};
		
		HashMap<String, Object> obj2 = new HashMap<String, Object>(){
			{
			     put("gender", "Female");
			     put("university", "DCU");

			}
		};
		
		HashMap<String, Object> obj3 = new HashMap<String, Object>(){
			{
			     put("gender", "Female");
			     put("university", "UCD");

			}
		};
		
		expectedResult.add(obj1);
		expectedResult.add(obj2);
		expectedResult.add(obj3);
		
		mockMvc.perform(post("/crosstabComp")
	            .params(params))
	    		.andExpect(status().isOk())
	    		.andExpect(jsonPath("$[0]", is(obj1)))
	            .andExpect(jsonPath("$[1]", is(obj2)))
	            .andExpect(jsonPath("$[2]", is(obj3)));
	}

}
