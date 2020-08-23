function createPDF() {
	
    var pdf = new jsPDF('l', 'pt', 'a4'); //Creating PDF object
    pdf.internal.scaleFactor = 1.33;
    var items = $(".grid-stack .grid-stack-item"); //Getting all items from grid
    var deferreds = [];
    
    var pdfName = document.getElementById('dashboardName').innerText + '.pdf'; //Sets name of pdf to be exported to the name of the dashboard specified by the user 
    
    //Iterating through items which have been added to the dashboard
    for (i = 0; i < items.length; i++) {

        var pos = $(items[i]).position();
        var numberGraphs = $(items[i]).find("#chartContent").length; //Checking if there are graph divs present in the html object
        
    	if (numberGraphs > 0){ 
    		var chartId = $(items[i]).find('canvas')[0].id; //Getting individual chart id of current chart being processed
    		addImage(pdf, pos, chartId);		
    	}
    	else {
    		var widgetId = $(items[i])[0].id;
    		var widget = document.getElementById(widgetId); //Getting chart by id
    	    var widgetWidth = widget.getBoundingClientRect().width * 0.75; //Multiply by 0.75 to convert to pt
            var widgetHeight = widget.getBoundingClientRect().height * 0.75; //Multiply by 0.75 to convert to pt
	    	var deferred = $.Deferred(); //Creating deferred object
	        deferreds.push(deferred.promise());
	        generateCanvasWidget(widgetId, pdf, deferred, widgetWidth, widgetHeight, pos);
    	}
    }
    
	$.when.apply($, deferreds).then(function () { // Executes after all images have been added
		pdf.save(pdfName);
		uploadPDF(pdf, pdfName);
	});
	
}
	 

function generateCanvasWidget(widgetId, pdf, deferred, widgetWidth, widgetHeight, pos){
    html2canvas(document.getElementById(widgetId), {
        onrendered: function (canvas) {
            var img = canvas.toDataURL("image/png", 1.0);
            pdf.addImage(img, 'JPEG', pos.left * 0.75, pos.top * 0.75, widgetWidth, widgetHeight); //Adding generated canvas image to existing pdf object
            deferred.resolve(); //Resolving the deferred
         }
    });
};


function addImage(pdf, pos, chartId) {
	var canvas = document.getElementById(chartId); //Getting chart by id
    var canvasImg = canvas.toDataURL("image/png", 1.0); //Converting chart to an image
    var imgWidth = canvas.getBoundingClientRect().width * 0.75; //Multiply by 0.75 to convert to pt
    var imgHeight = canvas.getBoundingClientRect().height * 0.75; //Multiply by 0.75 to convert to pt
    /* (x, y) co-ordinates of top left corner of image is (pos.left * 0.75, pos.right * 0.75)
     and multiplying the position px by 0.75 to convert to pt */
    pdf.addImage(canvasImg, 'JPEG', pos.left * 0.75, pos.top * 0.75, imgWidth, imgHeight); //Adding image to the pdf
};

function uploadPDF(pdf, pdfName) {
	var pdfString = pdf.output('datauristring'); //Converting PDF to Base64 string
	var token = $("meta[name='_csrf']").attr("content"); // Specifying CSRF tokens
	
	var pdfData = new FormData();     //Creating new FormData object to pass PDF string to controller
	pdfData.append('pdf', pdfString);
	pdfData.append('pdfName', pdfName);
	
	
	var params = {
	   type: 'post',
	   headers: {"X-CSRF-TOKEN": token},
	   url: '/uploadPDF',
	   data: pdfData,
	   processData: false,
	   contentType: false,
    };  
    
    $.ajax(params);
}
