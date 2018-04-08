package fi.soveltia.content.targeting.rule.openweathermap.provider;

import com.liferay.ip.geocoder.IPGeocoder;
import com.liferay.ip.geocoder.IPInfo;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.netflix.hystrix.HystrixEventType;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.content.targeting.rule.openweathermap.constants.Weather;
import fi.soveltia.content.targeting.rule.openweathermap.net.GetWeatherCommand;

@Component(
		immediate = true
)
public class OpenWeatherMapProvider {

	public OpenWeatherMapProvider() {
		
	}
	
	public OpenWeatherMapProvider(String appId, String ipAddress, int cacheTimeout, int connectionTimeout) {
		_appId = appId; 
		_ipAddress = ipAddress; 
		_cacheTimeout = cacheTimeout;
		_connectionTimeout = connectionTimeout;
	}
	
	/**
	 * Get possible messages
	 * 
	 * @return
	 */
	public List<String> getEvents() {
		return _events;
	}
	
	/** 
	 * Get temperature 
	 * 
	 * @param appId
	 * @param ipAddress
	 * @param useCache
	 * @return
	 */
	public Double getTemperature() {

		Double value = null;

		JSONObject weatherObject = getUserWeatherObject();
		
		if (weatherObject != null) {
			value = weatherObject.getJSONObject("main").getDouble("temp");
		}
		
		_log.debug("Temperature: " + value);

		return value;
	}

	/**
	 * Get weather 
	 * 
	 * @param appId
	 * @param ipAddress
	 * @param useCache
	 * @return
	 */
	public String getWeather() {
		
		String value = null; 

		JSONObject weatherObject = getUserWeatherObject();
		
		if (weatherObject != null) {

			int weatherCode = weatherObject.getJSONArray("weather").getJSONObject(0).getInt("id");

			_log.debug("Weathercode: " + weatherCode);

			Weather weather = Weather.getWeatherDefinition(weatherCode);

			if (weather != null) {
				value =  weather.name();
			}
		}
		
		return value;
	}
 	
	@Reference(unbind = "unsetIPGeocoder")
	public void setIPGeocoder(IPGeocoder ipGeocoder) {
		_ipGeocoder = ipGeocoder;
	}

	public void unsetIPGeocoder(IPGeocoder ipGeocoder) {
		_ipGeocoder = null;
	}

	/**
	 * Get user weather object
	 * 
	 * @param ipAddress
	 * @return String 
	 */
	public JSONObject getUserWeatherObject() {

		_log.debug("getUserWeatherObject()");		

		if (isCacheable()) {

			JSONObject cachedValue = _portalCache.get(_ipAddress);
			
			if (cachedValue != null) {
	 
				if (_log.isDebugEnabled()) {
					_log.debug("Weather object found from cache: " + cachedValue.toString());
				}
				
				return cachedValue;
			}
		}

		IPInfo ipInfo = _ipGeocoder.getIPInfo(_ipAddress);

		if (ipInfo == null) {
			return null;
		}
 
		float latitude = ipInfo.getLatitude();
		float longitude = ipInfo.getLongitude();
		
		// Syntax: http://www.openweathermap.com/current

		String location = HttpUtil.addParameter(API_URL, "lat", String.valueOf(latitude));
		location = HttpUtil.addParameter(location, "lon", String.valueOf(longitude));
		location = HttpUtil.addParameter(location, "units", "metric");
		location = HttpUtil.addParameter(location, "format", "json");
		location = HttpUtil.addParameter(location, "APPID", _appId);

		GetWeatherCommand getWeatherCommand = new GetWeatherCommand(location, _connectionTimeout);

		try {
			
			_log.debug("Call URL: " + location);
			
			String resultString = getWeatherCommand.execute();
			
			JSONObject result = null;
			
			if (resultString != null) {
				result = JSONFactoryUtil.createJSONObject(resultString);
			}
			
			if (result != null && isCacheable()) {
				_portalCache.put(_ipAddress, result, _cacheTimeout);
			}
			
			return result;

		} catch (Exception e) {
			_log.error(e, e);

		} finally {

			// Debugging  

			try {

				if (getWeatherCommand.getExecutionEvents().size() > 0) {
					_events = new ArrayList<String>();
					for (HystrixEventType e : getWeatherCommand.getExecutionEvents()) {
						_events.add(e.name());
					}
				}
				_events.add("Execution time in ms: " + getWeatherCommand.getExecutionTimeInMilliseconds());
				
			} catch (Exception e) {
				_log.error(e, e);
			}

		}

		return null;
	}	

	private boolean isCacheable() {
		return _cacheTimeout > 0;
	}
	
	@SuppressWarnings("unchecked")
	@Reference(unbind = "-")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache = 
			(PortalCache<String, JSONObject>)
			singleVMPool.getPortalCache(OpenWeatherMapProvider.class.getName());
	}	
	
	private static IPGeocoder _ipGeocoder;
	private String _appId;
	private int _cacheTimeout;
	private int _connectionTimeout;
	private List<String> _events;
	private String _ipAddress;	
	private PortalCache<String, JSONObject> _portalCache;
	
	private static final Log _log = LogFactoryUtil.getLog(OpenWeatherMapProvider.class);

	private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";
}
