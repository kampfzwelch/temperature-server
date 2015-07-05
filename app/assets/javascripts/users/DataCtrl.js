controllersModule.controller('testCtrl', function($route, $scope, $routeParams,
		TemperatureService, $log) {
	$scope.someData = {
		labels : [],
		datasets : [ {
			data : []
		} ]
	};
	$log.debug($routeParams.room);

	currentDate = new Date();

	$scope.today = function() {
		$scope.dt = currentDate;
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
			times.push(o.hour + ":" + o.minute);
		}

		data.forEach(pushIt);

		return {
			temperature : temps,
			time : times
		};
	};

	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();

		$scope.opened = true;
	};

	var callTempService = function() {
		$log.debug("Getting temperature for date " + $scope.dt.getDate());

		TemperatureService.getTemperature($routeParams.room,
				$scope.dt.getDate(), $scope.dt.getMonth() + 1,
				$scope.dt.getFullYear()).then(function(data) {
			var dd = splitData(data);
			$scope.temperatureData = dd;

			$scope.someData.datasets[0].data = dd.temperature;
			$scope.someData.labels = dd.time;
		}, function(reason) {
			alert('Failed: ' + reason);
		});
	}
	
	$scope.refreshTemperature = callTempService;

	callTempService();
});