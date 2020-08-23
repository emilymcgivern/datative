function addNewKpiNumTile(result, target, title) {
	
	//Creates a widget of cube shape
	var node = {width: 2, height: 3, autoPosition: true, noResize: true};
	var color;
	var tileId = Math.random();
	
	/*
	The following checks if the user has entered a target value, and if 
	the total value is greater than or less than the target value to 
	determine the color of the integer on the tile
	*/

	if (target != "") {
		if (result < target) {
			color = 'red';
		}

	    else {
			color = 'green';
		}
	}
	else {
		color = 'black';
	}
	
	if (grid.willItFit(null, null, 2, 3, true)) {
		grid.addWidget('<div id=tile' + tileId + '><div class="grid-stack-item-content"><div class="tileHeader"><div class="widgetTitle">' + title + '</div></div><div id="content" style="bottom:0;position:absolute;padding:5px;width:100%;"><div id="figure" style="float:right;"><font color=' + color + '>' + result + '</font></div></div></div></div>', node);
	}
	else {
		$("#dialog").dialog("open");
	}

				  
};
