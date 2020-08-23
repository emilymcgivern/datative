function addNewBarChart(response, title) {

	const datarr = []; 		// constant for data
	const xlabels = [];		// constant for labels

	const data = response.dimensionSums;
	for (i = 0; i < data.length; i++) {
		datarr.push(data[i]);
		xlabels.push(response.dimensions[i]);
	}

	const columnName = $("input[name='columnName']:checked").val(); //Getting value of chosen radio button

	//Position and dimensions of widget added
	const node = {width: 5, height: 5, autoPosition: true};
	const divId = Math.random();
	const barId = Math.random();

	if (grid.willItFit(null, null, 4, 4, true)) {
		grid.addWidget('<div id=barId' + barId + '><div class="grid-stack-item-content"><div class="barHeader"><div class="widgetTitle" id="widget-title-bar">' + title + '</div></div><div id="content" style="padding:5px;"><div id="chart-container"><canvas id="myChart_' + divId + '"></canvas></div></div></div>', node);
	} 
	else {
		$("#dialog").dialog("open");
	}

	const ctx = document.getElementById("myChart_" + divId).getContext('2d');

	const myChart = new Chart(ctx, {
		  type: 'bar',
		  data: {
		  	labels: xlabels,
		    datasets: [{
		  		label: columnName,
				data: datarr,
				fill: false,
                backgroundColor: getBarColour(response.dimensionSums.length),
				borderColor: "#3e95cd",
                borderWidth: 1
              }
            ]
          },
          options: {
  			tooltips: {enabled: false},
			hover: {mode: null},
			legend: {display: false},
		    responsive: true,
		    maintainAspectRatio: true,
	        title: {
	            display: true,
	            text: columnName
	        },
		    scales: {
		      xAxes: [{
		        ticks: {
		          maxRotation: 90,
		          minRotation: 80
		        }
		      }],
		      yAxes: [{
		        ticks: {
		          beginAtZero: true
		        }
		      }]
		    }
		  }
        });
	
	document.getElementById('barchartForm').reset(); 
};

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var colour = '#';
    for (var i = 0; i < 6; i++) {
        colour += letters[Math.floor(Math.random() * 16)];
    }
    return colour;
}

function getBarColour(dataLength) {
    var colours =[];
    for (var i = 0; i < dataLength; i++) {
    	colours.push(getRandomColor());
    }
    return colours;
}
