function addNewPieChart(response, title) {

	columnName = $("input[name='columnName']:checked").val(); //Getting value of chosen radio button

	//Position and dimensions of widget added
	node = {width: 5, height: 5, autoPosition: true};
	divId = Math.random();
	pieId = Math.random();

	if (grid.willItFit(null, null, 4, 4, true)) {
		grid.addWidget('<div id=pieId' + pieId + '><div class="grid-stack-item-content"><div class="pieHeader"><div class="widgetTitle" id="widget-title-pie">' + title + '</div></div><div id="content" style="padding:5px;"><div id="chart-container"><canvas id="myChart_' + divId + '"></canvas></div></div></div>', node);
	} 
	else {
		$("#dialog").dialog("open");
	}

	const ctx = document.getElementById("myChart_" + divId).getContext('2d');

	const myChart = new Chart(ctx, {
		  type: 'pie',
		  data: {
		  	labels: response.countLabels,
		    datasets: [{
		  		label: columnName,
				data: response.counts,
				fill: false,
                backgroundColor: getPieColour(response.counts.length),
              }
            ]
          },
          options: {
  			tooltips: {enabled: false},
			hover: {mode: null},
		    responsive: true,
		    maintainAspectRatio: true,
	        title: {
	            display: true,
	            text: columnName + ' %'
	        }
		  }
        });
	
	document.getElementById('piechartForm').reset(); 
};

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var colour = '#';
    for (var i = 0; i < 6; i++) {
        colour += letters[Math.floor(Math.random() * 16)];
    }
    return colour;
}

function getPieColour(dataLength) {
    var colours =[];
    for (var i = 0; i < dataLength; i++) {
    	colours.push(getRandomColor());
    }
    return colours;
}
