package com.example.datative.components;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.datative.dashboard.DashboardService;

@Service
public class Piechart {

	@Autowired
	DashboardService dashboardService;


	public Map<String, List> createPiechart(Map<String, String> barParams) {
		SparkSession spark = DashboardService.getSparkSession();
		Dataset<Row> columns = spark.sql("SELECT `" + barParams.get("columnName") + "` FROM tempTable");
		List<Row> columnList = columns.collectAsList(); //Creating list of relevant rows returned from SQL statement
		return getColumnValueCounts(columnList);
	}

	public Map<String, List> getColumnValueCounts(List<Row> columnList) {
		Map<Object, Long> valueCounts = columnList.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		List<Object> countNames = new ArrayList<Object>(valueCounts.keySet());
		List<Long> counts = new ArrayList<Long>();
		List<String> countLabels = new ArrayList<String>();
		
		for (Object item : countNames) {
			String countLabel = item.toString();
			countLabel = countLabel.substring(1, countLabel.length() - 1);
			countLabels.add(countLabel);
		}
		
        for (Map.Entry<Object, Long> entry : valueCounts.entrySet()) {
        	Long countValue = entry.getValue();
        	Integer columnListLength = columnList.size();
        	Long percentageValue = countValue * 100 / columnListLength;
        	counts.add(percentageValue);
        }

		Map<String, List> listMap = new HashMap<String, List>();
        listMap.put("countLabels", countLabels);
        listMap.put("counts", counts);

  		return listMap;
	}

}

