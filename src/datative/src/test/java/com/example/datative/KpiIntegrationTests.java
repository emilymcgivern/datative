package com.example.datative;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.example.datative.components.ComponentsController;
import com.example.datative.dashboard.DashboardService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class KpiIntegrationTests {
	
	@Autowired
	ComponentsController componentsController;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	
	@SuppressWarnings("serial")
	@Test
	public void kpiNumReachableAndAcceptsParams_PassIf200Repsonse() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.test_csv341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(){
			{
			     put("kpiType", Collections.singletonList("num"));
			     put("kpiTitle", Collections.singletonList("Confirmed Cases"));
			     put("kpiOp", Collections.singletonList("total"));
			     put("columnName", Collections.singletonList("No. of confirmed cases"));
			     put("kpiTarget", Collections.singletonList("2000"));
			}
		};

		mockMvc.perform(post("/kpiNumTarget")
	            .params(params))
	    		.andExpect(status().isOk()).andReturn();
	}
	
	@SuppressWarnings("serial")
	@Test
	public void kpiNumAcceptsMapReturnsDouble_PassIfEqual() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.test_csv341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(){
			{
			     put("kpiType", Collections.singletonList("num"));
			     put("kpiTitle", Collections.singletonList("Confirmed Cases"));
			     put("kpiOp", Collections.singletonList("total"));
			     put("columnName", Collections.singletonList("No. of confirmed cases"));
			     put("kpiTarget", Collections.singletonList("2000"));
			}
		};

		MvcResult result = mockMvc.perform(post("/kpiNumTarget")
	            .params(params))
	    		.andExpect(status().isOk()).andReturn();
		
		double content = Double.valueOf(result.getResponse().getContentAsString());
		assertEquals(content, 4138.0, 0.01);
	}

	

}
