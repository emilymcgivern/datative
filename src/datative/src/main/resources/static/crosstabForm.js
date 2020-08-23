function sendCrosstabForm(e) {
	e.preventDefault();
	var token = $("meta[name='_csrf']").attr("content"); // Specifying CSRF tokens 
	var title = $("#crosstabTitle").val();
	
	//  Ajax request to post to component controller 

	
	var params = {
		    type: 'post',
		    headers: {"X-CSRF-TOKEN": token},
		    data: $("#crosstabForm").serialize(),
		    url: '/crosstabComp',
		    success: function(response) {
		    	addCrosstab(response, title); 
			},
		};

	$.ajax(params);
	
}