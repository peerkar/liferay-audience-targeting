package fi.soveltia.content.targeting.rule.openweathermap.constants;

/**
 * Weather code to human readable form enum.
 * 
 * Thanks to https://github.com/gamerson/com.liferay.content.targeting.extensions.demo/blob/master/rule.weather/src/com/liferay/content/targeting/rule/weather/WeatherRule.java
 * 
 * @author peerkar
 *
 */
public enum Weather {

	// See http://openweathermap.org/weather-conditions
	
	THUNDERSTORM(new int[]{200, 231}), 
	DRIZZLE		(new int[]{300, 321}), 
	RAIN		(new int[]{500, 531}), 
	SNOW		(new int[]{600, 621}), 
	ATMOSPHERE	(new int[]{701, 781}), 
	CLEAR		(new int[]{800, 801}),
	CLOUDS		(new int[]{802, 804}), 
	EXTREME		(new int[]{900, 906});

	private Weather(int[] codeRange) {
		_codeRange = codeRange;
	}

	public static Weather getWeatherDefinition(int weatherCode) {

		for (Weather w : values()) {
			
			if (weatherCode >= w._codeRange[0] && weatherCode <= w._codeRange[1]) {
				return w;
			}
		}
		
		return null;
	}

	private final int[] _codeRange;

}
