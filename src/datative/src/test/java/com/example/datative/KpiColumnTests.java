package com.example.datative;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.datative.components.KpiTile;
import com.example.datative.dashboard.DashboardService;

@SpringBootTest
public class KpiColumnTests {

	@Autowired
	KpiTile kpiTile;
	
	@Autowired
	DashboardService dashboardService;	
	
	@SuppressWarnings("serial")
	@Test //Testing that a dataframe is created, correct columns are returned and correct result is computed.
	void checkGettingColumnsAndCalculationExecutedIntResult_PassIfEqual() {
		SparkSession spark = DashboardService.getSparkSession();
		String tableOption = "datative.test_csv341";
		Map<String, String> options = new HashMap<>();
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); 
		df.createOrReplaceTempView("tempTable"); 
		Map<String, String> params = new HashMap<String, String>(){
			{
			     put("kpiType", "num");
			     put("kpiTitle", "Confirmed Cases");
			     put("kpiOp", "total");
			     put("columnName", "No. of confirmed cases");
			     put("kpiTarget", "2000");
			}
		};
		
		Double result = kpiTile.getKpiIntegerResult(params);
		assertEquals(result, 4138, 0.01);
	}

}
