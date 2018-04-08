package fi.soveltia.content.targeting.rule.openweathermap;

import com.liferay.content.targeting.anonymous.users.model.AnonymousUser;
import com.liferay.content.targeting.api.model.Rule;
import com.liferay.content.targeting.model.RuleInstance;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
 * OpenWeatherMap Temperature Rule.
 * 
 * @author peerkar
 *
 */
@Component(
		immediate = true,
		service = Rule.class
)
public class TemperatureRule extends BaseOpenWeatherMapRule {

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
		
		int temperatureMin = typeSettingsObject.getInt(ContextKeys.TEMPERATURE_MIN);
		int temperatureMax = typeSettingsObject.getInt(ContextKeys.TEMPERATURE_MAX);

		// Get user temperature
		
		OpenWeatherMapProvider provider = getWeatherProvider(anonymousUser, typeSettingsObject);
		
		Double userTemperature = provider.getTemperature();
		
		if (Validator.isNull(userTemperature)) {

			_log.debug("Couldn't get user temperature.");

			return false;
		}
 
		_log.debug("Temperature min: " + temperatureMin);
		_log.debug("Temperature max: " + temperatureMax);
		_log.debug("User temperature: " + userTemperature);

		if (userTemperature >= temperatureMin && userTemperature <= temperatureMax) {
			
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
	
			int temperatureMin = typeSettingsObject.getInt(ContextKeys.TEMPERATURE_MIN);
			int temperatureMax = typeSettingsObject.getInt(ContextKeys.TEMPERATURE_MAX);
			 
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle("content.Language", locale, getClass());
	
			summary = ResourceBundleUtil.getString(
					resourceBundle, locale, "temperature-between-x-and-x",
					new Object[] {temperatureMin, temperatureMax});
			
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
		
		int temperatureMin = GetterUtil.getInteger(values.get(ContextKeys.TEMPERATURE_MIN));
		int temperatureMax = GetterUtil.getInteger(values.get(ContextKeys.TEMPERATURE_MAX));

		jsonObject.put(ContextKeys.TEMPERATURE_MIN, temperatureMin);
		jsonObject.put(ContextKeys.TEMPERATURE_MAX, temperatureMax);

		return jsonObject.toString();
	}

	@Override
	protected String getFormTemplatePath() {
		return _FORM_TEMPLATE_PATH;
	}
		
	@Override
	protected void populateContext(RuleInstance ruleInstance, Map<String, Object> context, Map<String, String> values) {

		_log.debug("populateContext()");
		
		populateCommonContext(ruleInstance, context, values);

		int temperatureMin = DEFAULT_TEMPERATURE_MIN;
		int temperatureMax = DEFAULT_TEMPERATURE_MAX;

		if (!values.isEmpty()) {

			// Value from the request in case of an error

			temperatureMin = GetterUtil.getInteger(values.get(ContextKeys.TEMPERATURE_MIN));
			temperatureMax = GetterUtil.getInteger(values.get(ContextKeys.TEMPERATURE_MAX));

		} else if (ruleInstance != null) {

			String typeSettings = ruleInstance.getTypeSettings();

			try {
				JSONObject jsonObj = JSONFactoryUtil.createJSONObject(typeSettings);

				temperatureMin = jsonObj.getInt(ContextKeys.TEMPERATURE_MIN);
				temperatureMax = jsonObj.getInt(ContextKeys.TEMPERATURE_MAX);

			} catch (JSONException e) {
				_log.error(e, e);
			}
		}

		context.put(ContextKeys.TEMPERATURE_MIN, temperatureMin);
		context.put(ContextKeys.TEMPERATURE_MAX, temperatureMax);
	}

	public static final int DEFAULT_TEMPERATURE_MIN = -50;
	public static final int DEFAULT_TEMPERATURE_MAX = 50;
	
	private static final String _FORM_TEMPLATE_PATH = "/view_temperature.jsp";	

	private static final Log _log = LogFactoryUtil.getLog(TemperatureRule.class);
}
