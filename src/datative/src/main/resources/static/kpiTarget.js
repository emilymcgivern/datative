function addNewKpiTargetTile(result, target, title) {
	
	//Creates a widget of cube shape
	var node = {width: 2, height: 3, autoPosition: true, noResize: true};
	var short, numArray, pieColours, borderColours, borderWidths ;
	var divId = Math.random();
	var tileId = Math.random();
	
	if (target > result) {
		short = target - result;
		numArray = [result, short];
		pieColours = ['red' , '#dce0e6'];		
	}
	
	else {
		numArray = [result];
		pieColours = ['green'];
	}
	
	
	if (grid.willItFit(null, null, 2, 3, true)) {
		grid.addWidget('<div id=tile' + tileId + '><div class="grid-stack-item-content"><div class="tileHeader"><div class="widgetTitle">' + title + '</div></div><div id="content" style="bottom:0;position:absolute;padding:5px;width:100%;"><div id="chart-container" style="height:70px; width:70px;float:left;"><canvas id="myChart_' + divId + '"></canvas></div><div id="values" style = "height:60px; width:70px;float:right;margin-top:10px;"><p style="font-size:12px;margin:0">' + result + '</p><p style="font-size:10px;margin:0">Target: ' + target + '</p></div></div></div></div>', node);
	}
	else {
		$("#dialog").dialog("open");
	}
    
	
	const ctx = document.getElementById("myChart_" + divId).getContext('2d');
    	
	var myChart = new Chart(ctx, {
		  type: 'doughnut',
		  data: {
		    datasets: [{
		      data: numArray,
		      fill: true,
		      backgroundColor: pieColours,
		    }]
		  },
		  options: {
		    elements: {
		        arc: {
		            borderWidth: 0
		        }
		    },
			tooltips: {enabled: false},
			hover: {mode: null},
		    responsive: true,
		    maintainAspectRatio: false,
		    cutoutPercentage: 80
		  }
		});
    
};