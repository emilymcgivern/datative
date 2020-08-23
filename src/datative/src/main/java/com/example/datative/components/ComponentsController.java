package com.example.datative.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ComponentsController {
    
    @Autowired
    Crosstab crosstab;
    
    @Autowired
    KpiTile kpiTile;
    
    @Autowired
    Barchart barchart;
    
    @Autowired
    Piechart piechart;


    @PostMapping("/kpiNumTarget")
    @ResponseBody
    public Double kpiNum(@RequestParam Map<String, String> params) {
    	double val = kpiTile.getKpiIntegerResult(params);
        return val;
    }

    @SuppressWarnings("rawtypes")
	@PostMapping("/kpiTrend")
    @ResponseBody
    public Map<String, List> kpiTrend(@RequestParam Map<String, String> params) {
    	Map<String, List> valuesMap = kpiTile.getKpiResultsMap(params);
    	return valuesMap;

    }

    @PostMapping("/crosstabComp")
    @ResponseBody
    public ArrayList<HashMap<String, Object>> crosstabComp(@RequestParam Map<String, String> requestParams) {	//Gathering all request params into a map
    	ArrayList<HashMap<String, Object>> crosstabValues = crosstab.createCrosstab(requestParams);
    	return crosstabValues;
    }


    @PostMapping("/barchart")
    @ResponseBody
    public Map<String, List> createBarchart(@RequestParam Map<String, String> barParams) {
    	Map<String, List> valuesMap = barchart.createBarchart(barParams);
    	return valuesMap;

    }
    
    @PostMapping("/piechart")
    @ResponseBody
    public Map<String, List> createPieChart(@RequestParam Map<String, String> pieParams) {
    	Map<String, List> valuesMap = piechart.createPiechart(pieParams);
    	return valuesMap;

    }
    

}
