function sendPiechartForm(e) {
	e.preventDefault();
	var token = $("meta[name='_csrf']").attr("content"); // Specifying CSRF tokens 
	var title = $("#pieTitle").val();
	
	var params = {
		    type: 'post',
		    headers: {"X-CSRF-TOKEN": token},
		    data: $("#piechartForm").serialize(), 
		    url: '/piechart',
		    success: function(response) {
		    	addNewPieChart(response, title); 
			},
			async: true,
		};

	$.ajax(params);
}