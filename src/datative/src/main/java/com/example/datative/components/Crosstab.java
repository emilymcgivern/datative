package com.example.datative.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.datative.dashboard.DashboardService;

@Service
public class Crosstab {
	
	@Autowired
	DashboardService dashboardService; 

	public ArrayList<HashMap<String, Object>> createCrosstab(Map<String, String> requestParams) {
		SparkSession spark = DashboardService.getSparkSession();
		if (requestParams.get("opName").equals("count")) {
			Dataset<Row> columns = spark.sql("SELECT `" + requestParams.get("columnName") + "`, `" + requestParams.get("rowName") + "` FROM tempTable");
			List<Row> columnList = columns.collectAsList();
			ArrayList<HashMap<String, Object>> crosstabCount = new ArrayList<HashMap<String, Object>>();
			for (Row item : columnList) {
				HashMap<String, Object> valuesMap = new HashMap<String, Object>(); //Creating new HashMap which will contain each set of row values
				valuesMap.put(requestParams.get("columnName"), item.get(0));
				valuesMap.put(requestParams.get("rowName"), item.get(1));
				crosstabCount.add(valuesMap); //Adding HashMap of values to ArrayList
			}
			
			return crosstabCount;
		}
		
		else {
			Dataset<Row> columns = spark.sql("SELECT `" + requestParams.get("columnName") + "`, `" + requestParams.get("rowName") + "`, `" + requestParams.get("aggregateName") + "`  FROM tempTable");
			List<Row> columnList = columns.collectAsList();
			ArrayList<HashMap<String, Object>> crosstabSum = new ArrayList<HashMap<String, Object>>();
			for (Row item : columnList) {
				HashMap<String, Object> valuesMap = new HashMap<String, Object>(); //Creating new HashMap which will contain each set of row values
				valuesMap.put(requestParams.get("columnName"), item.get(0));
				valuesMap.put(requestParams.get("rowName"), item.get(1));
				valuesMap.put("value", item.get(2));
				crosstabSum.add(valuesMap); //Adding HashMap of values to ArrayList
			}	
			
			return crosstabSum;
		}      
	}
	

}
