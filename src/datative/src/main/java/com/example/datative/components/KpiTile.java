package com.example.datative.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.datative.dashboard.DashboardService;

@Service
public class KpiTile {
	 
	@Autowired
	DashboardService dashboardService;
	
	
	public Double getKpiIntegerResult(Map<String, String> params) {
		SparkSession spark = DashboardService.getSparkSession();
		Dataset<Row> columnContent = spark.sql("SELECT `" + params.get("columnName") + "` FROM tempTable"); //Select chosen column from table
		List<Row> columnList = columnContent.collectAsList(); //Collecting rows from previous query into a list
		double result = performNumOps(columnList, params.get("kpiOp"));
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, List> getKpiResultsMap(Map<String, String> params) {
		SparkSession spark = DashboardService.getSparkSession();
		Dataset<Row> columns = spark.sql("SELECT `" + params.get("columnName") + "`, `" + params.get("columnNameTrendDate") + "` FROM tempTable");
		List<Row> columnList = columns.collectAsList(); //Creating list of relevant rows returned from SQL statement
		return getDateTotals(columnList);
	}
	
	public Double performNumOps(List<Row> columnList, String opName) {
		double result = 0;
		Double[] numsList = new Double[columnList.size()]; //Creating an array with size of length of list of columns

		//Iterating through list of rows and adding first value to integer list so that operations can be performed on list
		for (int i = 0; i < columnList.size(); i++) {
			Row item = columnList.get(i); //Get corresponding row
			if (item.get(0) instanceof Integer) {
				int v = (int)item.get(0);
				double doubleV = v;
				numsList[i] = doubleV;
			}
			else {
				double v = (double)item.get(0);
				numsList[i] = v;
			}
		}
		
		//User selects total operation to be performed
    	if (opName.equals("total")) {
    		double sum = 0;
    		for (int i = 0; i < numsList.length; i++)
    		    sum += numsList[i];
    		result = sum;
    	}
    	
    	//User selects average operation to be performed
    	else if (opName.equals("average")) {
    		if (numsList.length == 0) {
    			result = 0;
    		}
    		else {
    			double total = 0;
    			for (int i = 0; i < numsList.length; i++) 
    				total += numsList[i];
    				double average = total / numsList.length;
    			result = average;
    		}
      		  
    	}
    	
    	//User selects median operation to be performed
    	else if (opName.equals("med")) {
    		double med = 0;
    		Arrays.sort(numsList);
    		 		
    		if (numsList.length % 2 == 0) 
    			med = (numsList[(numsList.length / 2) - 1] + numsList[numsList.length / 2]) / 2;
    		else
    			med = numsList[(numsList.length -1) / 2];
    		
    		result = med;
    	}
    	
		return Math.round(result * 100.0) / 100.0; //Rounding results to 2 decimal places

	}
	
	
	@SuppressWarnings("rawtypes")
	public Map<String, List> getDateTotals(List<Row> columnList) {
        Map<Date, List<Row>> groupByDate = columnList.stream().collect(Collectors.groupingBy(i -> (Date) i.get(1))); //Groups returned columns by date
		Map<Date, List<Row>> sortedDateMap = new TreeMap<>(groupByDate);
        
        List<Date> dateLabels = new ArrayList<Date>(sortedDateMap.keySet()); //Getting each individual date
        List<Double> dateSums = new ArrayList<Double>(); 
        
        //Getting sum of values for each date to show trend over time
        for (Entry<Date, List<Row>> entry : sortedDateMap.entrySet()) {
        	List<Row> dateEntries = entry.getValue();
        	double total = 0;
        	for (Row item : dateEntries) {
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
        	
        	dateSums.add(Math.round(total * 100.0) / 100.0);
        }
        
     
        Map<String, List> listMap = new HashMap<String, List>();
        listMap.put("dates", dateLabels);
        listMap.put("dateSums", dateSums);
        
  		return listMap;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map<String, List> getDimensionTotals(List<Row> columnList) {
		Map<Object, List<Row>> groupByDimension = columnList.stream().collect(Collectors.groupingBy(i -> i.get(1)));
		Map<Object, List<Row>> sortedDimensionMap = new TreeMap<>(groupByDimension);
		
		List<Object> dimensionLabels = new ArrayList<Object>(sortedDimensionMap.keySet());
		List<Double> dimensionSums = new ArrayList<Double>(); 
               
        //Getting sum of values for each date to show trend over time
        for (Entry<Object, List<Row>> entry : sortedDimensionMap.entrySet()) {
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
