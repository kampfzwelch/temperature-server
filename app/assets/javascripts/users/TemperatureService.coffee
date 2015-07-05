
class TemperatureService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing TemperatureService"

    getTemperature: (roomName, day, month, year) ->
        @$log.debug "getTemperature() for " + roomName.toLowerCase() + "On day: " + day + " On month: " + (month) + " On year" + year; 
        deferred = @$q.defer()

        @$http.get("/temperature/" + roomName.toLowerCase() + "/" + day + "/" + month + "/" + year)
        .success((data, status, headers) =>
                @$log.info("Successfully listed temp - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list temp - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

servicesModule.service('TemperatureService', TemperatureService)