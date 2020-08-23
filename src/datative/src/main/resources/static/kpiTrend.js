function addNewKpiTrendTile(result, title) {
	
	//Creates a widget of cube shape
	var node = {width: 2, height: 3, autoPosition: true, noResize: true};
	var color;
	var divId = Math.random();
	var tileId = Math.random();
	
	if (grid.willItFit(null, null, 2, 3, true)) {
		grid.addWidget('<div id=tile' + tileId + '><div class="grid-stack-item-content"><div class="tileHeader"><div class="widgetTitle">' + title + '</div></div><div class="chart-container" style="position: relative; height:110px; width:150px"><canvas id="myChart_' + divId + '"></canvas></div></div></div>', node);
	}
	else {
		$("#dialog").dialog("open");
	}

    
	const ctx = document.getElementById("myChart_" + divId).getContext('2d');
    	
	var myChart = new Chart(ctx, {
		  type: 'line',
		  data: {
		    labels: result.dates,
		    datasets: [{
		      data: result.dateSums,
		      borderColor: "#3e95cd"
		    }]
		  },
		  options: {
			tooltips: {enabled: false},
			hover: {mode: null},
			legend: {display: false},
		    responsive: true,
		    maintainAspectRatio: false,
		    scales: {
		      xAxes: [{
		        ticks: {
		          maxRotation: 90,
		          minRotation: 80,
		          fontSize: 10,
		          display: true,
		          autoSkip: true,
		          maxTicksLimit: 2,
		          fontSize: 8
		        }
		      }],
		      yAxes: [{
		        ticks: {
		          beginAtZero: true,
		          maxTicksLimit: 5,
		          fontSize: 10
		        }
		      }]
		    }
		  }
		});   
};
