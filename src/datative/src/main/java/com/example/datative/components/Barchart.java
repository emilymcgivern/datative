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
public class Barchart {

	@Autowired
	DashboardService dashboardService;


	public Map<String, List> createBarchart(Map<String, String> barParams) {
		SparkSession spark = DashboardService.getSparkSession();
		Dataset<Row> columns = spark.sql("SELECT `" + barParams.get("columnName") + "`, `" + barParams.get("columnNameDimensionCompBar") + "` FROM tempTable");
		List<Row> columnList = columns.collectAsList(); //Creating list of relevant rows returned from SQL statement
		return getDimensionTotals(columnList);

	}

	public Map<String, List> getDimensionTotals(List<Row> columnList) {
		Map<Object, List<Row>> groupByDimension = columnList.stream().collect(Collectors.groupingBy(i -> i.get(1)));
		Map<Object, List<Row>> sortedDimensionMap = new TreeMap<>(groupByDimension);

		List<Object> dimensionLabels = new ArrayList<Object>(sortedDimensionMap.keySet());
		List<Double> dimensionSums = new ArrayList<Double>();

        //Getting sum of values for each date to show trend over time
        for (Map.Entry<Object, List<Row>> entry : sortedDimensionMap.entrySet()) {
        	List<Row> dimensionEntries = entry.getValue();
        	double total = 0;
        	for (Row item : dimensionEntries) {
        		if (item.get(0) instanceof Integer) {
        			int v = (int) item.get(0);
        			double numVal = v;
        			total += numVal;
        		}
        		else {
        			double numVal = (double)item.get(0);
        			total += numVal;
        		}
        	}

        	dimensionSums.add(Math.round(total * 100.0) / 100.0);
        }

		Map<String, List> listMap = new HashMap<String, List>();
        listMap.put("dimensions", dimensionLabels);
        listMap.put("dimensionSums", dimensionSums);

  		return listMap;
	}

}
