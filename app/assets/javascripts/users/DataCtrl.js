controllersModule.controller('testCtrl', function($route, $scope, $routeParams,
		TemperatureService, $log) {
	$scope.dayData = {
		labels : [],
		datasets : [ {
			data : []
		} ]
	};
	
	$log.debug($routeParams.room);

	$scope.today = function() {
		$log.debug("Today function called!");
		$scope.dt = new Date();;
	};

	$scope.today();

	$scope.reloadRoute = function() {
		$route.reload();
	}

	$scope.room = $routeParams.room;

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

	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();

		$scope.opened = !$scope.opened;
	};

	var callTempService = function() {
		$log.debug("Getting temperature for date " + $scope.dt.getDate());

		TemperatureService.getTemperature($routeParams.room,
				$scope.dt.getDate(), $scope.dt.getMonth() + 1,
				$scope.dt.getFullYear()).then(function(data) {
			var dd = splitData(data);
			$scope.temperatureData = dd;

			$scope.dayData.datasets[0].data = dd.temperature;
			$scope.dayData.labels = dd.time;
		}, function(reason) {
			alert('Failed: ' + reason);
		});
	}
	
	$scope.refreshTemperature = callTempService;

	$scope.colorVal = "redBg";
	
	callTempService();
});