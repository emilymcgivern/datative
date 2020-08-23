package com.example.datative;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.datative.components.ComponentsController;
import com.example.datative.components.Crosstab;
import com.example.datative.dashboard.DashboardService;

@SpringBootTest
public class CrosstabCountTests {
	
	@Autowired
	Crosstab crossTab;
	
	@Autowired
	ComponentsController crosstabController;
	
	@SuppressWarnings("serial")
	@Test
	public void checkCreateCrosstabResultStringColRow_PassIfEqual() {	
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.student_test341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable"); 
		Map<String, String> params = new HashMap<String, String>() {
			{
				put("opName", "count");
				put("columnName", "gender");
				put("rowName", "university");
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
		
		ArrayList<HashMap<String, Object>> result = crossTab.createCrosstab(params);
		assertEquals(expectedResult, result);
	}
}
