function addCrosstab(valuesList, title) {
	
	//Getting label value of checked radio buttons
	var columnName = $('#crosstabColumnName').find('option:selected').text().trim();
	var rowName = $('#crosstabRowName').find('option:selected').text().trim();
	var opName = $('#crosstabOpName').find('option:selected').text().trim();
	var aggName = $('#crosstabAggName').find('option:selected').text().trim();
	
	var node = {width: 2, height: 3, autoPosition: true};
	var crosstabId = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 9); //Generating random substring to create unique div id
    
	var sum = $.pivotUtilities.aggregatorTemplates.sum;
    var numberFormat = $.pivotUtilities.numberFormat;
    var intFormat = numberFormat({digitsAfterDecimal: 0});
    
	if (grid.willItFit(null, null, 2, 3, true)) {
		grid.addWidget('<div id="crosstab' + crosstabId + '"><div class="grid-stack-item-content" style="text-align: center;"><div class="crosstabHeader"><div class="widgetTitle">' + title + '</div></div><div id="crosstabContent' + crosstabId + '" style="display: inline-block;"></div></div></div>', node);
	}
	else {
		$("#dialog").dialog("open");
	}
	
	var params = {
			rows: [rowName],
			cols: [columnName]
	}
	
	if (opName === 'Sum') {
		params.aggregator = sum(intFormat)(["value"]) //Adding aggregator if sum is selected operation
	}
        
    $("#crosstabContent" + crosstabId).pivot(
    		valuesList, //List of HashMaps containing set of values to display in crosstab
    		params
     );
    
	document.getElementById('crosstabForm').reset(); 
	//Hiding divs after form has been submitted so it is reset to original view
	$('#choose-column-crosstab').hide();
	$('#choose-row-crosstab').hide();
	$('#choose-agg-crosstab').hide();
				  
};

