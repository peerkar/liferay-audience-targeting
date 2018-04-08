package fi.soveltia.content.targeting.rule.openweathermap;

import com.liferay.content.targeting.anonymous.users.model.AnonymousUser;
import com.liferay.content.targeting.api.model.Rule;
import com.liferay.content.targeting.model.RuleInstance;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import fi.soveltia.content.targeting.rule.openweathermap.constants.ContextKeys;
import fi.soveltia.content.targeting.rule.openweathermap.provider.OpenWeatherMapProvider;

/**
 * OpenWeatherMap weather rule
 *
 * @author peerkar
 *
 */
@Component(
		immediate = true, 
		service = Rule.class
)
public class WeatherRule extends BaseOpenWeatherMapRule {

	@Activate
	@Override
	public void activate() {
		super.activate();
	}

	@Deactivate
	@Override
	public void deActivate() {
		super.deActivate();
	}

	@Override
	public boolean evaluate(HttpServletRequest httpServletRequest, RuleInstance ruleInstance,
			AnonymousUser anonymousUser) throws Exception {

		_log.debug("evaluate()");

		// Get rule configuration from the type settings

		JSONObject typeSettingsObject = JSONFactoryUtil.createJSONObject(ruleInstance.getTypeSettings());

		String weather = typeSettingsObject.getString(ContextKeys.WEATHER);

		// Get user weather
		
		OpenWeatherMapProvider provider = getWeatherProvider(anonymousUser, typeSettingsObject);
		
		String userWeather = provider.getWeather();

		if (Validator.isNull(userWeather)) {

			_log.debug("Couldn't get user weather");

			return false;
		}
		
		_log.debug("Weather: " + weather);
		_log.debug("User weather: " + userWeather);
		
		if (weather.equals(userWeather)) {

			_log.debug("Rule matches.");
			
			return true;
		}

		_log.debug("Rule doesn't match.");

		return false;
	}

	@Override
	public String getSummary(RuleInstance ruleInstance, Locale locale) {

		String summary = StringPool.DASH;

		try {
			JSONObject typeSettingsObject = JSONFactoryUtil.createJSONObject(ruleInstance.getTypeSettings());
	
			String weather = typeSettingsObject.getString(ContextKeys.WEATHER);
			
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle("content.Language", locale, getClass());
	
			summary = ResourceBundleUtil.getString(
					resourceBundle, locale, "weather-equals-x",
					new Object[] {weather});
		} catch (JSONException e) {
			_log.error(e, e);
		}
		
		return summary;	
	}	
	
	@Override
	public String processRule(PortletRequest portletRequest, PortletResponse portletResponse, String id,
			Map<String, String> values) {

		_log.debug("processRule()");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		
		processRuleCommonVariables(jsonObject, values);

		String weather = values.get(ContextKeys.WEATHER);

		jsonObject.put(ContextKeys.WEATHER, weather);

		return jsonObject.toString();
	} 
	
	@Override
	protected String getFormTemplatePath() {
		return _FORM_TEMPLATE_PATH;
	}

	@Override
	protected void populateContext(RuleInstance ruleInstance, Map<String, Object> context, Map<String, String> values) {

		populateCommonContext(ruleInstance, context, values);
		
		String weather = null;

		if (!values.isEmpty()) {

			// Value from the request in case of an error

			weather = values.get(ContextKeys.WEATHER);

		} else if (ruleInstance != null) {

			String typeSettings = ruleInstance.getTypeSettings();

			try {

				JSONObject jsonObj = JSONFactoryUtil.createJSONObject(typeSettings);

				weather = jsonObj.getString(ContextKeys.WEATHER);

			} catch (JSONException e) {
				_log.error(e, e);
			}
		}

		context.put(ContextKeys.WEATHER, weather);
	}

	private static final String _FORM_TEMPLATE_PATH = "/view_weather.jsp";

	private static final Log _log = LogFactoryUtil.getLog(WeatherRule.class);
}