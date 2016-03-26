controllersModule.controller('dataCtrl', function($route, $routeParams,
		TemperatureService, $log) {
	
	var data = this;
	
	data.dayData = {
		labels : [],
		datasets : [ {
			data : []
		} ]
	};
	
	data.today = function() {
		$log.debug("Today function called!");
		data.dt = new Date();;
	}();

	data.room = $routeParams.room;

	function splitData(data) {
		var temps = [];
		var times = [];

		function pushIt(o) {
			temps.push(o.temp);
			times.push(o.hour + ":" + o.minute + "0");
		}

		data.forEach(pushIt);

		return {
			temperature : temps.slice(Math.max(temps.length - 24, 0)),
			time : times.slice(Math.max(times.length - 24, 0))
		};
	};

	data.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();

		data.opened = !data.opened;
	};

	var callTempService = function() {
		$log.debug("Getting temperature for date " + data.dt.getDate());

		TemperatureService.getTemperature($routeParams.room,
				data.dt.getDate(), data.dt.getMonth() + 1,
				data.dt.getFullYear()).then(function(tempData) {
			var dd = splitData(tempData);
			data.temperatureData = dd;

			data.dayData.datasets[0].data = dd.temperature;
			data.dayData.labels = dd.time;
		}, function(reason) {
			alert('Failed: ' + reason);
		});
	}
	
	data.refreshTemperature = callTempService;

	data.colorVal = "redBg";
	
	callTempService();
});