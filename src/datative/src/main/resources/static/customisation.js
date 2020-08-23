var currentSelection;

function getCurrentSelection(curr) {
	currentSelection = curr; //Getting value of selected item
}

function headerColour(colour) {
	currentSelection.find('.widgetTitle').css('color', colour);
}

function boldText() {
	currentSelection.find('.widgetTitle').toggleClass('bold'); 
}

function italicText() {
	currentSelection.find('.widgetTitle').toggleClass('italic');
}

function underlineText() {
	currentSelection.find('.widgetTitle').toggleClass('underline');
}

function fontFamily(font) {
	currentSelection.find('.widgetTitle').css('font-family', font);
}

function widgetBackgroundColour(colour) {
	currentSelection.find('.grid-stack-item-content').css('background-color', colour);
}

function resetStyleOptions() {
	$('#bold').removeClass('clicked');	
	$('#italic').removeClass('clicked');
	$('#underline').removeClass('clicked');
	$('#fontFamily').val('Arial').prop('selected', true);
	$("#color-picker-header").spectrum("set", "#010101");
	$("#color-picker-background").spectrum("set", "#fafafa");
}

function storeStyleState(prevDiv) {
	if (prevDiv != "") {
		var fontColour = prevDiv.find('.widgetTitle').css('color');
    	var backgroundColour = prevDiv.find('.grid-stack-item-content').css('background-color');
	}
	var clickedButtons = $('.textButton.clicked').map(function () {return this.id;}).get(); //Getting id of clicked buttons and returning map
    var styleValues = JSON.stringify({ //Adding json string representation of user chosen values
        font:document.getElementById('fontFamily').value,
        fontcolour:fontColour,
        bgcolour:backgroundColour,
        clickedButtons:clickedButtons
    });
    
	localStorage.setItem(prevId, styleValues);
}

function populateDiv(prevId, prevDiv) {
	resetStyleOptions(prevDiv);
	var styleValues = JSON.parse(localStorage.getItem(prevId)); 
	if (styleValues != null) {
		$('#fontFamily').val(styleValues.font).prop('selected', true);
		for (var i = 0; i < (styleValues.clickedButtons).length; i++){
			var currButton = styleValues.clickedButtons[i]
			$('#' + currButton).toggleClass('clicked');	
		}
		$("#color-picker-header").spectrum("set", styleValues.fontcolour);
		$("#color-picker-background").spectrum("set", styleValues.bgcolour);
	}
}