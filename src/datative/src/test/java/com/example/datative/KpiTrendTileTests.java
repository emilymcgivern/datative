package com.example.datative;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.jupiter.api.Test;
import org.spark_project.guava.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.datative.components.KpiTile;

@SpringBootTest
class KpiTrendTileTests {
	
	@Autowired
	KpiTile kpiTile;
	
	//Tests of Operations when choice is total
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkPosWholeDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListPos = ImmutableList.of(RowFactory.create(482, date1), RowFactory.create(604, date1), RowFactory.create(614, date2), RowFactory.create(1, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListPos);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(1086.0, 615.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkNegWholeDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListNeg = ImmutableList.of(RowFactory.create(-25, date1), RowFactory.create(-62, date1), RowFactory.create(-36, date2), RowFactory.create(-102, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListNeg);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(-87.0, -138.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkMixedWholeDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListMixed = ImmutableList.of(RowFactory.create(-25, date1), RowFactory.create(37, date1), RowFactory.create(-2, date1), RowFactory.create(-36, date2), RowFactory.create(-102, date2), RowFactory.create(74, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListMixed);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(10.0, -64.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkPosDoubleDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListMixed = ImmutableList.of(RowFactory.create(25.0, date1), RowFactory.create(37.0, date1), RowFactory.create(2.0, date1), RowFactory.create(36.0, date2), RowFactory.create(102.0, date2), RowFactory.create(74.0, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListMixed);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(64.0, 212.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkNegDoubleDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListMixed = ImmutableList.of(RowFactory.create(-25.0, date1), RowFactory.create(-37.0, date1), RowFactory.create(-2.0, date1), RowFactory.create(-36.0, date2), RowFactory.create(-102.0, date2), RowFactory.create(-74.0, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListMixed);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(-64.0, -212.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkMixedDoubleDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = dateformat.parse("2014-02-11");
		Date date2 = dateformat.parse("2014-02-12");
		List<Row> valuesListMixed = ImmutableList.of(RowFactory.create(-25.0, date1), RowFactory.create(37.0, date1), RowFactory.create(-2.0, date1), RowFactory.create(-36.0, date2), RowFactory.create(-102.0, date2), RowFactory.create(74.0, date2));
		Map<String, List> result = kpiTile.getDateTotals(valuesListMixed);
		List<Date> testDateLabels = Arrays.asList(date1, date2);
		List<Double> testSums = Arrays.asList(10.0, -64.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkEmptyList_PassIfEqual() { 
		List<Row> emptyList = ImmutableList.of();
		Map<String, List> result = kpiTile.getDateTotals(emptyList);
		List<Date> testDateLabels = Arrays.asList();
		List<Double> testSums = Arrays.asList();
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	@Test //Mix of positive and negative values with 2 dimensions
	void checkSingleValueDateList_PassIfEqual() throws ParseException { 
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateformat.parse("2014-02-11");
		List<Row> valuesListSingle = ImmutableList.of(RowFactory.create(25, date));
		Map<String, List> result = kpiTile.getDateTotals(valuesListSingle);
		List<Date> testDateLabels = Arrays.asList(date);
		List<Double> testSums = Arrays.asList(25.0);
		
		Map<String, List> expectedResults  = new HashMap<String, List>() {{
		    put("dates", testDateLabels);
		    put("dateSums", testSums);
		}};
		
		assertEquals(result, expectedResults);	
	}
	
}

