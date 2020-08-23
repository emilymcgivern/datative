package com.example.datative;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.datative.cleaning.DataCleaning;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DataCleaningTests {
	
	@Autowired
	DataCleaning dataCleaning;

	String[] names = {"John", "Joe", "Jim", "Jess"};
	double[] averages = {90.1, 84.3, 99.7, 37.2};
	Integer[] ages = {30, 54, 24, 25};

	String[] missingStrings = {null, "Jim", "Jane", "Kate"};
	String[] replacedstrings = {"None", "Jim", "Jane", "Kate"};
	
	String[] sortedNames = {"Jess", "Jim", "Joe", "John"};
	double[] sortedAverages = {37.2, 99.7, 84.3, 90.1};
	Integer[] sortedAges = {25, 24, 54, 30};
	
	String[] emptyColumn = {"", "", "", ""};
	double[] sortedAveragesEmptyTest = {37.2, 84.3, 90.1, 99.7};
	Integer[] sortedAgesEmptyTest = {25, 54, 30, 24};
	
	
	
	@Test
	public void testRemoveDuplicateRows_PassIfRemoved() {
		Table tester = Table.create("Tester Table")
							.addColumns(
							StringColumn.create("name", names),
							DoubleColumn.create("average", averages),
							IntColumn.create("age", ages));
		
		int rowCount = tester.rowCount();
		
		Table tableCopy = Table.create("Tester Table Copy")
				.addColumns(
						StringColumn.create("name", names),
						DoubleColumn.create("average", averages),
						IntColumn.create("age", ages));
		
		tester.append(tableCopy);
	    assertEquals(rowCount * 2, tester.rowCount());
	    Table newTable = DataCleaning.removeDuplicateRows(tester);
		assertThat(rowCount, is(equalTo(newTable.rowCount())));
	}
	
	@Test
	public void testReplaceMissingValues_PassIfEqual() {
		Table tester = Table.create("Tester Table").addColumns(
						StringColumn.create("name", missingStrings),
						DoubleColumn.create("average", averages),
						IntColumn.create("age", ages));

    	Table expectedResult = Table.create("Tester Table").addColumns(
						StringColumn.create("name", replacedstrings),
						DoubleColumn.create("average", averages),
						IntColumn.create("age", ages));
    	
    	String currName = "name";
    	Column<?> curr = tester.column("name");
    	int colSize = curr.size();
    	
    	DataCleaning.replaceMissingValues(curr, currName, tester, colSize);
    	
    	assertEquals(tester.toString() , expectedResult.toString());	
    	
	}
	
	@Test
	public void testCleaningFullTableNoWrongData_PassIfEqual() {
		Table tester = Table.create("Tester Table").addColumns(
						StringColumn.create("name", names),
						DoubleColumn.create("average", averages),
						IntColumn.create("age", ages));

    	Table expectedResult = Table.create("Tester Table").addColumns(
						StringColumn.create("name", sortedNames),
						DoubleColumn.create("average", sortedAverages),
						IntColumn.create("age", sortedAges));
    	
    	
    	Table result = DataCleaning.cleanData(tester);
    	
    	assertEquals(result.toString() , expectedResult.toString());	
    	
	}
	
	@Test
	public void testCleaningFullTableEmptyColumn_PassIfEqual() {
		Table tester = Table.create("Tester Table").addColumns(
						StringColumn.create("name", emptyColumn),
						DoubleColumn.create("average", averages),
						IntColumn.create("age", ages));

    	Table expectedResult = Table.create("Tester Table").addColumns(
						DoubleColumn.create("average", sortedAveragesEmptyTest),
						IntColumn.create("age", sortedAgesEmptyTest));
    	
    	
    	Table result = DataCleaning.cleanData(tester);
    	
    	assertEquals(result.toString() , expectedResult.toString());	
    	
	}
	
	@Test
	public void testCleaningFullTableFromFileMissingData_PassIfEqual() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
        URL testResource = classLoader.getResource("datacleaning-test.csv");
        URL resultsResource = classLoader.getResource("cleaned.csv");
		Table table = Table.read().csv(CsvReadOptions.builder(testResource).tableName("Tester table"));
		Table expectedResult = Table.read().csv(CsvReadOptions.builder(resultsResource).tableName("Tester table"));
		Table result = DataCleaning.cleanData(table);
		assertEquals(result.toString() , expectedResult.toString());
	}
	
	

}
