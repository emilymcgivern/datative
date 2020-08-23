function sendKpiForm(e) {
	e.preventDefault();
	var token = $("meta[name='_csrf']").attr("content"); // Specifying CSRF tokens 
	var kpiType = $("#kpiType").val();
	var target = $("#kpiTarget").val();
	var title = $("#kpiTitle").val();
	var tester = $("#tester").text();

	//  Ajax request to post to component controller 
	//	Choosing where to post data based on users choice of KPI tile
	
	var params = {
		    type: 'post',
		    headers: {"X-CSRF-TOKEN": token},
		    success: function(response) {
		    	if (kpiType === 'num') {
		    		addNewKpiNumTile(response, target, title); //Passing result of post request to function
		    	}
		    	else if (kpiType === 'trend') {
		    		addNewKpiTrendTile(response, title); //Passing result of post request to function
		    	}
		    	else if (kpiType === 'target') {
		    		addNewKpiTargetTile(response, target, title);
		    	}
			},
		    data: $("#kpiForm").serializeArray()
		};
	
		if (kpiType == 'num' || kpiType == 'target') {
		    params.url ='/kpiNumTarget';
		} 
		
		else if (kpiType == 'trend') {
			params.url ='/kpiTrend';
		}
		
	$.ajax(params);
	document.getElementById('kpiForm').reset(); //Resetting form so that another tile can be added
	
	//Hiding divs after form has been submitted so it is reset to original view
	$('#choose-column').hide();
	$('#choose-column-trend-date').hide();
	$('#kpi-op').hide();
	$('#choose-target').hide();
	$('#choose-name').hide();
	
}