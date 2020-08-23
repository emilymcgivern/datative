package com.example.datative;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.spark_project.guava.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.datative.components.KpiTile;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class KpiNumTileTests {
	
	@Autowired
	KpiTile kpiTile;

	static List<Row> emptyList = ImmutableList.of();
	static List<Row> positiveWhole = ImmutableList.of(RowFactory.create(482), RowFactory.create(604), RowFactory.create(322), RowFactory.create(614));
	static List<Row> positiveWholeOdd = ImmutableList.of(RowFactory.create(482), RowFactory.create(604), RowFactory.create(322), RowFactory.create(614), RowFactory.create(219));
	static List<Row> positiveDouble = ImmutableList.of(RowFactory.create(482.64), RowFactory.create(604.925), RowFactory.create(322.3), RowFactory.create(614.6662));
	static List<Row> positiveDoubleOdd = ImmutableList.of(RowFactory.create(482.64), RowFactory.create(604.925), RowFactory.create(322.3), RowFactory.create(614.6662), RowFactory.create(418.23));	
	static List<Row> negativeWhole = ImmutableList.of(RowFactory.create(-482), RowFactory.create(-604), RowFactory.create(-322), RowFactory.create(-614));
	static List<Row> negativeWholeOdd = ImmutableList.of(RowFactory.create(-482), RowFactory.create(-604), RowFactory.create(-322), RowFactory.create(-614), RowFactory.create(-563));
	static List<Row> negativeDouble = ImmutableList.of(RowFactory.create(-482.64), RowFactory.create(-604.925), RowFactory.create(-322.3), RowFactory.create(-614.6662));
	static List<Row> negativeDoubleOdd = ImmutableList.of(RowFactory.create(-482.64), RowFactory.create(-604.925), RowFactory.create(-322.3), RowFactory.create(-614.6662), RowFactory.create(-532.36));
	static List<Row> mixedWhole = ImmutableList.of(RowFactory.create(-482), RowFactory.create(604), RowFactory.create(-322), RowFactory.create(614), RowFactory.create(916));
	static List<Row> mixedWholeEven = ImmutableList.of(RowFactory.create(-482), RowFactory.create(604), RowFactory.create(-322), RowFactory.create(614));
	static List<Row> mixedDouble = ImmutableList.of(RowFactory.create(-482.64), RowFactory.create(-604.925), RowFactory.create(322.3), RowFactory.create(-614.6662), RowFactory.create(94.13));
	static List<Row> mixedDoubleEven = ImmutableList.of(RowFactory.create(-482.64), RowFactory.create(-604.925), RowFactory.create(322.3), RowFactory.create(-614.6662));
	static List<Row> zeroWhole = ImmutableList.of(RowFactory.create(0));
	static List<Row> zeroDouble = ImmutableList.of(RowFactory.create(0.0));
	static List<Row> singlePosWhole = ImmutableList.of(RowFactory.create(25));
	static List<Row> singlePosDouble = ImmutableList.of(RowFactory.create(25.3));
	static List<Row> singleNegWhole = ImmutableList.of(RowFactory.create(-25));
	static List<Row> singleNegDouble = ImmutableList.of(RowFactory.create(-25.3));
	static List<Row> mixedList = ImmutableList.of(RowFactory.create(482.35), RowFactory.create(-604), RowFactory.create(322.3), RowFactory.create(614.36), RowFactory.create(-93.2), RowFactory.create(61));
	static List<Row> mixedListOdd = ImmutableList.of(RowFactory.create(482.35), RowFactory.create(-604), RowFactory.create(322.3), RowFactory.create(614.36), RowFactory.create(-93.2));
		
	//Tests of Operations when choice is total
	
	@Test //Normal positive list - Whole nums
	void checkPosWholeNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(positiveWhole, "total");
		assertEquals(result, 2022, 0);	
	}
	
	@Test //Normal positive list - Double nums
	void checkPosDoubleNumOpsTotal_PassIfEqual() { 
		double result = kpiTile.performNumOps(positiveDouble, "total");
		assertEquals(result, 2024.53, 0.01);	
	}
	
	@Test //Normal negative list - Whole nums
	void checkNegWholeNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeWhole, "total");
		assertEquals(result, -2022, 0);	
	}
	
	@Test //Normal negative list - Double nums
	void checkNegDoubleNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeDouble, "total");
		assertEquals(result, -2024.53, 0);	
	}
	
	@Test //Mixed Pos and Neg List - Whole Nums
	void checkMixedWholeNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedWhole, "total");
		assertEquals(result, 1330, 0);	
	}
	
	@Test //Mixed Pos and Neg List - Double Nums
	void checkMixedDoubleNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedDouble, "total");
		assertEquals(result, -1285.80, 0);	
	}
	
	@Test //Only zero Value in List - Whole
	void checkZeroWholeNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroWhole, "total");
		assertEquals(result, 0, 0);		
	}
	
	@Test //Only zero Value in List - Double
	void checkZeroDoubleNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroDouble, "total");
		assertEquals(result, 0.0, 0);		
	}
	
	@Test //One Pos Value List - Whole Num
	void checkSinglePosWholeNumNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosWhole, "total");
		assertEquals(result, 25, 0);
	}
	
	@Test //One Pos Value List - Double Num
	void checkSinglePosDoubleNumNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosDouble, "total");
		assertEquals(result, 25.3, 0);
	}
	
	@Test //One Neg Value List - Whole Num
	void checkSingleNegWholeNumNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegWhole, "total");
		assertEquals(result, -25, 0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkSingleNegDoubleNumNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegDouble, "total");
		assertEquals(result, -25.3, 0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkMixedNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedList, "total");
		assertEquals(result, 782.81, 0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkEmptyListNumOpsTotal_PassIfEqual() {
		double result = kpiTile.performNumOps(emptyList, "total");
		assertEquals(result, 0, 0);
	}
	
	//Tests of operations when choice is average
	
	@Test //Normal positive list - Whole nums
	void checkPosWholeNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(positiveWhole, "average");
		assertEquals(result, 505.5, 0.0);	
	}
	
	@Test //Normal positive list - Double nums
	void checkPosDoubleNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(positiveDouble, "average");
		assertEquals(result, 506.13, 0.0);	
	}
	
	@Test //Normal negative list - Whole nums
	void checkNegWholeNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeWhole, "average");
		assertEquals(result, -505.5, 0.0);	
	}
	
	@Test //Normal negative list - Double nums
	void checkNegDoubleNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeDouble, "average");
		assertEquals(result, -506.13, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Whole Nums
	void checkMixedWholeNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedWhole, "average");
		assertEquals(result, 266, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Double Nums
	void checkMixedDoubleNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedDouble, "average");
		assertEquals(result, -257.16, 0.0);	
	}
	
	@Test //Only zero Value in List - Whole
	void checkZeroWholeNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroWhole, "average");
		assertEquals(result, 0, 0.0);		
	}
	
	@Test //Only zero Value in List - Double
	void checkZeroDoubleNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroDouble, "average");
		assertEquals(result, 0.0, 0.0);		
	}
	
	@Test //One Pos Value List - Whole Num
	void checkSinglePosWholeNumNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosWhole, "average");
		assertEquals(result, 25, 0.0);
	}
	
	@Test //One Pos Value List - Double Num
	void checkSinglePosDoubleNumNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosDouble, "average");
		assertEquals(result, 25.3, 0.0);
	}
	
	@Test //One Neg Value List - Whole Num
	void checkSingleNegWholeNumNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegWhole, "average");
		assertEquals(result, -25, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkSingleNegDoubleNumNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegDouble, "average");
		assertEquals(result, -25.3, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkMixedNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedList, "average");
		assertEquals(result, 130.47, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkEmptyListNumOpsAverage_PassIfEqual() {
		double result = kpiTile.performNumOps(emptyList, "average");
		assertEquals(result, 0, 0.0);
	}

//	///Tests of operations when choice is median
//	
	@Test //Normal positive list with even number of values - Whole nums
	void checkPosWholeNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(positiveWhole, "med");
		assertEquals(result, 543, 0.0);	
	}
	
	@Test //Normal positive list with odd number of values - Whole nums
	void checkPosWholeNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(positiveWholeOdd, "med");
		assertEquals(result, 482, 0.0);	
	}
	
	@Test //Normal positive list - Double nums
	void checkPosDoubleNumEvenOpsMedian_PassIfEqual() { 
		double result = kpiTile.performNumOps(positiveDouble, "med");
		assertEquals(result, 543.78, 0.0);	
	}
	
	@Test //Normal positive list - Double nums
	void checkPosDoubleNumOddOpsMedian_PassIfEqual() { 
		double result = kpiTile.performNumOps(positiveDoubleOdd, "med");
		assertEquals(result, 482.64, 0.0);		
	}
	
	@Test //Normal negative list - Whole nums
	void checkNegWholeNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeWhole, "med");
		assertEquals(result, -543, 0.0);	
	}
	
	@Test //Normal negative list - Whole nums
	void checkNegWholeNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeWholeOdd, "med");
		assertEquals(result, -563, 0.0);	
	}
	
	@Test //Normal negative list - Double nums
	void checkNegDoubleNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeDouble, "med");
		assertEquals(result, -543.78, 0.0);	
	}
	
	@Test //Normal negative list - Double nums
	void checkNegDoubleNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(negativeDoubleOdd, "med");
		assertEquals(result, -532.36, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Whole Nums
	void checkMixedWholeNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedWhole, "med");
		assertEquals(result, 604, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Whole Nums
	void checkMixedWholeNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedWholeEven, "med");
		assertEquals(result, 141, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Double Nums
	void checkMixedDoubleNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedDouble, "med");
		assertEquals(result, -482.64, 0.0);	
	}
	
	@Test //Mixed Pos and Neg List - Double Nums
	void checkMixedDoubleNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedDoubleEven, "med");
		assertEquals(result, -543.78, 0.0);	
	}
	
	@Test //Only zero Value in List - Whole
	void checkZeroWholeNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroWhole, "med");
		assertEquals(result, 0, 0.0);		
	}
	
	@Test //Only zero Value in List - Double
	void checkZeroDoubleNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(zeroDouble, "med");
		assertEquals(result, 0.0, 0.0);		
	}
	
	@Test //One Pos Value List - Whole Num
	void checkSinglePosWholeNumNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosWhole, "med");
		assertEquals(result, 25, 0.0);
	}
	
	@Test //One Pos Value List - Double Num
	void checkSinglePosDoubleNumNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(singlePosDouble, "med");
		assertEquals(result, 25.3, 0.0);
	}
	
	@Test //One Neg Value List - Whole Num
	void checkSingleNegWholeNumNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegWhole, "med");
		assertEquals(result, -25, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkSingleNegDoubleNumNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(singleNegDouble, "med");
		assertEquals(result, -25.3, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkMixedNumEvenOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedList, "med");
		assertEquals(result, 191.65, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkMixedNumOddOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(mixedListOdd, "med");
		assertEquals(result, 322.3, 0.0);
	}
	
	@Test //One Neg Value List - Double Num
	void checkEmptyListNumOpsMedian_PassIfEqual() {
		double result = kpiTile.performNumOps(emptyList, "Median");
		assertEquals(result, 0, 0.0);
	}
	
}

