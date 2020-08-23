function sendBarchartForms(e) {
	e.preventDefault();
	var token = $("meta[name='_csrf']").attr("content"); // Specifying CSRF tokens 
	var title = $("#barTitle").val();
	
	var params = {
		    type: 'post',
		    headers: {"X-CSRF-TOKEN": token},
		    data: $("#barchartForm").serialize(), //Getting selected data from form
		    url: '/barchart',
		    success: function(response) {
		    	addNewBarChart(response, title); //If the request is successful this function is called
			},
			async: true,
		};

	$.ajax(params);
}