package fi.soveltia.content.targeting.rule.openweathermap.display.context;

import com.liferay.content.targeting.display.context.BaseRuleDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.GetterUtil;

import fi.soveltia.content.targeting.rule.openweathermap.constants.ContextKeys;
import fi.soveltia.content.targeting.rule.openweathermap.constants.Weather;

public class OpenWeatherMapRuleDisplayContext extends BaseRuleDisplayContext {

	public OpenWeatherMapRuleDisplayContext(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletRequest, liferayPortletResponse);
	}

	public String getAppId() {
		
		if (_appId != null) {
			return _appId;
		}

		_appId = GetterUtil.getString(context.get(ContextKeys.APP_ID));

		return _appId;
	}
	
	public String getCacheTimeout() {
		
		if (_cacheTimeout != null) { 
			return _cacheTimeout;
		}

		_cacheTimeout = String.valueOf(context.get(ContextKeys.CACHE_TIMEOUT));

		return _cacheTimeout;
	}	
	
	public String getConnectionTimeout() {
		
		if (_connectionTimeout != null) { 
			return _connectionTimeout;
		}

		_connectionTimeout = String.valueOf(context.get(ContextKeys.CONNECTION_TIMEOUT));

		return _connectionTimeout;
	}		

	public String getTemperatureMin() {
		
		if (_temperatureMin != null) {
			return _temperatureMin;
		}

		_temperatureMin = String.valueOf(context.get(ContextKeys.TEMPERATURE_MIN));
		
		return _temperatureMin;
	}

	public String getTemperatureMax() {
		
		if (_temperatureMax != null) {
			return _temperatureMax;
		}

		_temperatureMax = String.valueOf(context.get(ContextKeys.TEMPERATURE_MAX));

		return _temperatureMax;
	}
	
	public String getTestIpAddress() {
		
		if (_testIpAddress != null) { 
			return _testIpAddress;
		}

		_testIpAddress = GetterUtil.getString(context.get(ContextKeys.TEST_IP_ADDRESS));

		return _testIpAddress;
	}	
	
	public String getWeather() {
		
		if (_weather != null) {
			return _weather;
		}

		_weather = GetterUtil.getString(context.get(ContextKeys.WEATHER));

		return _weather;
	}

	public String[] getWeatherOptions() {
		
		if (_weatherOptions != null) {
			return _weatherOptions;
		}
		
		_weatherOptions = new String[Weather.values().length];
		
		int i = 0;
		for (Weather w : Weather.values()) {
			_weatherOptions[i] = w.name();
			i++;
		}

		return _weatherOptions;
	}

	private String _appId;
	private String _cacheTimeout;
	private String _connectionTimeout;
	private String _temperatureMin;
	private String _temperatureMax;
	private String _testIpAddress;
	private String _weather;
	private String[] _weatherOptions;

}