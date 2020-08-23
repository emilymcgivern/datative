package com.example.datative.cleaning;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

@Service
public class DataCleaning {
	
	public static Table cleanData(Table dataSet) { 	
		manageMissingData(dataSet);
		Table cleanedDataSet = removeDuplicateRows(dataSet);
		return cleanedDataSet;

	}
	
	public static void manageMissingData(Table dataSet) {
		checkColumnTypes(dataSet);
		List<String> columnNames = dataSet.columnNames();
		for (int i = 0; i < columnNames.size(); i++) {
        	String currName = columnNames.get(i);
        	Column<?> curr = dataSet.column(currName);
        	int missing = curr.countMissing(); 
        	int colSize = curr.size();
        	double percentageColSize = colSize * 0.30; 
       
        	if (colSize == missing) {
        		dataSet.removeColumns(curr);   //If the column is empty, it is removed  		
        	}   
        	else if (missing >= percentageColSize) {
        		dataSet.removeColumns(curr);
        	}
      
        	else {
        		replaceMissingValues(curr, currName, dataSet, colSize);
        	}
        }

	}
	
	public static Table checkColumnTypes(Table dataSet) {
		List<String> columnNames = dataSet.columnNames();
		
		for (int i = 0; i < columnNames.size(); i++) {
        	String currName = columnNames.get(i);
        	String cString = dataSet.column(currName).type().toString();
        	int count, intCount, doubleCount, dateCount;
        	count = intCount = doubleCount = dateCount = 0; //Initializing all counts to 0
        	
        	
        	if (cString == "STRING") {
        		StringColumn ss = dataSet.stringColumn(currName);
        		for (String entry : ss) {
        			try {
        				boolean doubleCheck = Pattern.matches("\\d+\\.\\d+", entry);
        				boolean dateCheck = Pattern.matches("[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{4}", entry);
        				if (doubleCheck) {
        					doubleCount += 1;
        				}
        				else if (dateCheck) {
        					dateCount += 1;
        				}
        				else {
        					Integer.parseInt(entry);
            				intCount += 1;
        				}

        			}
        			catch (Exception e) {
        				count += 1;
        			}
        		}
        	
        		
        		if (intCount > count) {
        			IntColumn newIntColumn = IntColumn.create(currName);
	        		for (String entry : ss) {
	        			try {
	        				int newInt = Integer.parseInt(entry);
	        				newIntColumn.append(newInt);
	        			}
	        			catch (Exception e) {
	        				newIntColumn.append(0);
	
	        			}
	        		}
	        		
	        		dataSet.removeColumns(currName);
	        		dataSet.addColumns(newIntColumn);
        		}
        		
        		else if (doubleCount > count) {
                	DoubleColumn newDoubleColumn = DoubleColumn.create(currName);
	        		for (String entry : ss) {
	        			boolean doubleCheck = Pattern.matches("\\d+\\.\\d+", entry);
        				if (doubleCheck) {
        					double d = Double.parseDouble(entry); 
        					newDoubleColumn.append(d);
        				}
        				else {
        					newDoubleColumn.append(0.0);
	        			}
	        		}
	        		
	        		dataSet.removeColumns(currName);
	        		dataSet.addColumns(newDoubleColumn);
        		}
        		
        		else if (dateCount > count) {
                	DateColumn newDateColumn = DateColumn.create(currName);
            		DateTimeFormatter formatter = getFormatter();
            		DateTimeFormatter mdyFormatter = getMDYFormatter();

	        		for (String entry : ss) {
	        			boolean dateCheck = Pattern.matches("[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{2,4}", entry);
        				if (dateCheck) {
        					try {
        						LocalDate localDate = LocalDate.parse(entry, formatter);  
            					newDateColumn.append(localDate);
        					} catch (DateTimeParseException e) {
        						LocalDate localDate = LocalDate.parse(entry, mdyFormatter);  
            					newDateColumn.append(localDate);
        					}
        				}
        				else {
        					newDateColumn.append(LocalDate.of(1900, 01, 01)); //Value to add when date is missing
	        			}
	        		}
	        		
	        		dataSet.removeColumns(currName);
	        		dataSet.addColumns(newDateColumn);
        		}
        		
        		else {
	        		for (String entry : ss) {
	        			try {
	        				boolean doubleCheck = Pattern.matches("\\d+\\.\\d+", entry);
	        				boolean dateCheck = Pattern.matches("[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{2,4}", entry);
	        				if (doubleCheck || dateCheck) {
	        					ss.set(ss.isEqualTo(entry), "None");
	        				}
	        				else {
	        					Integer.parseInt(entry);
	        					ss.set(ss.isEqualTo(entry), "None");
	        				}
	        			}
	        			catch (Exception e) {	        			
	        			}
	        		}
        		}
        	}
        	
        	if (cString == "DATE") {
        		convertDateColumn(currName, dataSet);
        	}
        }
		
		return dataSet;
		
	}
	
	public static void convertDateColumn(String currName, Table dataSet) {
		StringColumn dateC = dataSet.stringColumn(currName);
		
		DateTimeFormatter formatter = getFormatter();
		int i = 0;
		
		for (String entry : dateC) {
			DateColumn d = dataSet.dateColumn(currName);
			LocalDate localDate = LocalDate.parse(entry, formatter);  
			d.set(i, localDate);
			i++; 
			
		}		
	}
	
	public static Table removeDuplicateRows(Table dataSet) {
		Table result = dataSet.dropDuplicateRows();
    	return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void replaceMissingValues(Column curr, String currName, Table dataSet, int colSize) {
		double percentageColSize = colSize * 0.30;
    	ColumnType colType = curr.type();
    	String cString  = colType.toString();
    	
    	if (cString == "INTEGER") {
    		IntColumn c = dataSet.intColumn(currName);
    		int avg = (int) Math.round(c.mean());
    		curr.setMissingTo(avg);
    	}
    	
    	else if (cString == "STRING") {
    		curr.setMissingTo("None");
    		StringColumn strCol = dataSet.stringColumn(currName);
    		int numEmpty = strCol.countOccurrences("None");
    		if (numEmpty >= percentageColSize) {
    			dataSet.removeColumns(currName);
    		}
    	}
    	
    	else if (cString == "DOUBLE") {
    		DoubleColumn c = dataSet.doubleColumn(currName);
    		double avg = Math.round(c.mean());
    		curr.set(curr.isMissing(), avg);
    	
    	}
	}
	
	public static DateTimeFormatter getFormatter() {
		//Adding Date Formatter Type 1
		DateTimeFormatter date0 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter date1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
		DateTimeFormatter date2 = DateTimeFormatter.ofPattern("dd/MM/yy");
		DateTimeFormatter date3 = DateTimeFormatter.ofPattern("dd-MM-yy"); 
		DateTimeFormatter date4 = DateTimeFormatter.ofPattern("dd-M-yyyy"); 
		DateTimeFormatter date5 = DateTimeFormatter.ofPattern("d-M-yyyy"); 
		DateTimeFormatter date6 = DateTimeFormatter.ofPattern("dd-M-yy"); 
		DateTimeFormatter date7 = DateTimeFormatter.ofPattern("d-M-yyyy"); 
		DateTimeFormatter date8 = DateTimeFormatter.ofPattern("dd/M/yyyy"); 
		DateTimeFormatter date9 = DateTimeFormatter.ofPattern("d/M/yyyy"); 
		DateTimeFormatter date10 = DateTimeFormatter.ofPattern("dd/M/yy"); 
		DateTimeFormatter date11 = DateTimeFormatter.ofPattern("d/M/yyyy");
		DateTimeFormatter date12 = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
		DateTimeFormatter date13 = DateTimeFormatter.ofPattern("yyyy/MM/dd"); 
		DateTimeFormatter date14 = DateTimeFormatter.ofPattern("yy-MM-dd"); 
		DateTimeFormatter date15 = DateTimeFormatter.ofPattern("yy/MM/dd"); 

		DateTimeFormatter time0 = DateTimeFormatter.ofPattern("HH:mm:ss"); 
		DateTimeFormatter time1 = DateTimeFormatter.ofPattern("HH:mm"); 
		DateTimeFormatter time2 = DateTimeFormatter.ofPattern("HH:mm:ss a"); 
		DateTimeFormatter time3 = DateTimeFormatter.ofPattern("HH:mm: a"); 

		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			      	  .appendOptional(date0)
			          .appendOptional(date1)
			          .appendOptional(date2)
			          .appendOptional(date3)
			      	  .appendOptional(date4)
			          .appendOptional(date5)
			          .appendOptional(date6)
			          .appendOptional(date7)
			          .appendOptional(date8)
			          .appendOptional(date9)
			          .appendOptional(date10)
			          .appendOptional(date11)
			          .appendOptional(date12)
			          .appendOptional(date13)
			          .appendOptional(date14)
			          .appendOptional(date15)
			      	  .appendOptional(time0)
			          .appendOptional(time1)
			          .appendOptional(time2)
			          .appendOptional(time3)
			          .toFormatter();
		
		return formatter;
	}
	
	public static DateTimeFormatter getMDYFormatter() {
		//Adding Date Formatter Type 1
		DateTimeFormatter date0 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		DateTimeFormatter date1 = DateTimeFormatter.ofPattern("MM-dd-yyyy");  
		DateTimeFormatter date2 = DateTimeFormatter.ofPattern("MM/dd/yy");
		DateTimeFormatter date3 = DateTimeFormatter.ofPattern("MM-dd-yy"); 
		DateTimeFormatter date4 = DateTimeFormatter.ofPattern("M-dd-yyyy"); 
		DateTimeFormatter date5 = DateTimeFormatter.ofPattern("M-d-yyyy"); 
		DateTimeFormatter date6 = DateTimeFormatter.ofPattern("M-dd-yy"); 
		DateTimeFormatter date7 = DateTimeFormatter.ofPattern("M-d-yyyy"); 
		DateTimeFormatter date8 = DateTimeFormatter.ofPattern("M/dd/yyyy"); 
		DateTimeFormatter date9 = DateTimeFormatter.ofPattern("M/d/yyyy"); 
		DateTimeFormatter date10 = DateTimeFormatter.ofPattern("M/dd/yy"); 
		DateTimeFormatter date11 = DateTimeFormatter.ofPattern("M/d/yyyy");
		DateTimeFormatter date12 = DateTimeFormatter.ofPattern("yyyy-dd-MM"); 
		DateTimeFormatter date13 = DateTimeFormatter.ofPattern("yyyy/dd/MM"); 
		DateTimeFormatter date14 = DateTimeFormatter.ofPattern("yy-dd-MM"); 
		DateTimeFormatter date15 = DateTimeFormatter.ofPattern("yy/dd/MM"); 

		DateTimeFormatter time0 = DateTimeFormatter.ofPattern("HH:mm:ss"); 
		DateTimeFormatter time1 = DateTimeFormatter.ofPattern("HH:mm"); 
		DateTimeFormatter time2 = DateTimeFormatter.ofPattern("HH:mm:ss a"); 
		DateTimeFormatter time3 = DateTimeFormatter.ofPattern("HH:mm: a"); 

		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			      	  .appendOptional(date0)
			          .appendOptional(date1)
			          .appendOptional(date2)
			          .appendOptional(date3)
			      	  .appendOptional(date4)
			          .appendOptional(date5)
			          .appendOptional(date6)
			          .appendOptional(date7)
			          .appendOptional(date8)
			          .appendOptional(date9)
			          .appendOptional(date10)
			          .appendOptional(date11)
			          .appendOptional(date12)
			          .appendOptional(date13)
			          .appendOptional(date14)
			          .appendOptional(date15)
			      	  .appendOptional(time0)
			          .appendOptional(time1)
			          .appendOptional(time2)
			          .appendOptional(time3)
			          .toFormatter();
		
		return formatter;
	}
}
