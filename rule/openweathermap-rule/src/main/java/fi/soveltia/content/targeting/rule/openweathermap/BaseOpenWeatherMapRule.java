package fi.soveltia.content.targeting.rule.openweathermap;

import com.liferay.content.targeting.anonymous.users.model.AnonymousUser;
import com.liferay.content.targeting.api.model.BaseJSPRule;
import com.liferay.content.targeting.model.RuleInstance;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Reference;

import fi.soveltia.content.targeting.rule.categories.OpenWeatherMapRuleCategory;
import fi.soveltia.content.targeting.rule.openweathermap.constants.ContextKeys;
import fi.soveltia.content.targeting.rule.openweathermap.provider.OpenWeatherMapProvider;

/**
 * Base OpenWeatherMap Rule
 * 
 * @author peerkar
 *
 */
public abstract class BaseOpenWeatherMapRule extends BaseJSPRule {

	@Override
	public String getIcon() {
		return "icon-puzzle-piece";
	}

	@Override
	public String getRuleCategoryKey() {
		return OpenWeatherMapRuleCategory.KEY;
	}		
	
	@Override
	@Reference(
		target = "(osgi.web.symbolicname=fi.soveltia.content.targeting.rule.openweathermap)",
		unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}
	
	/**
	 * Get OpenWeatherMap provider object.
	 * 
	 * @param anonymousUser
	 * @param typeSettingsObject
	 * @return
	 */
	protected OpenWeatherMapProvider getWeatherProvider(AnonymousUser anonymousUser, JSONObject typeSettingsObject) {

		String appId = typeSettingsObject.getString(ContextKeys.APP_ID);
		int cacheTimeout = typeSettingsObject.getInt(ContextKeys.CACHE_TIMEOUT);
		int connectionTimeout = typeSettingsObject.getInt(ContextKeys.CONNECTION_TIMEOUT);
		 
		// Use test ipaddress?

		String ipAddress;

		String testIpAddress = typeSettingsObject.getString(ContextKeys.TEST_IP_ADDRESS);

		// As this is a demo just falling back silently if manual IP not correct
		
		if (Validator.isNotNull(testIpAddress) && Validator.isIPAddress(testIpAddress)) {
			ipAddress = testIpAddress;
		} else {
			ipAddress = anonymousUser.getLastIp();
		}
		
		return new OpenWeatherMapProvider(appId, ipAddress, cacheTimeout, connectionTimeout);
	}
	
	/**
	 * Populate context common for all OpenWeatherMapRules
	 * 
	 * @param ruleInstance
	 * @param context
	 * @param values
	 */
	protected void populateCommonContext(RuleInstance ruleInstance, Map<String, Object> context, Map<String, String> values) {

		String appId = null;
		int cacheTimeout = DEFAULT_CACHE_TIMEOUT;
		int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
		String testIpAddress = null;

		if (!values.isEmpty()) {

			// Value from the request in case of an error

			appId = GetterUtil.getString(values.get(ContextKeys.APP_ID));
			cacheTimeout = GetterUtil.getInteger(values.get(ContextKeys.CACHE_TIMEOUT));
			connectionTimeout = GetterUtil.getInteger(values.get(ContextKeys.CONNECTION_TIMEOUT));
			testIpAddress = GetterUtil.getString(values.get(ContextKeys.TEST_IP_ADDRESS));

		} else if (ruleInstance != null) {

			String typeSettings = ruleInstance.getTypeSettings();

			try {
				JSONObject jsonObj = JSONFactoryUtil.createJSONObject(typeSettings);

				appId = jsonObj.getString(ContextKeys.APP_ID);
				cacheTimeout = jsonObj.getInt(ContextKeys.CACHE_TIMEOUT);
				connectionTimeout = jsonObj.getInt(ContextKeys.CONNECTION_TIMEOUT);
				testIpAddress = jsonObj.getString(ContextKeys.TEST_IP_ADDRESS);

			} catch (JSONException e) {
				_log.error(e, e);
			}
		}

		context.put(ContextKeys.APP_ID, appId);
		context.put(ContextKeys.CACHE_TIMEOUT, cacheTimeout);
		context.put(ContextKeys.CONNECTION_TIMEOUT, connectionTimeout);
		context.put(ContextKeys.TEST_IP_ADDRESS, testIpAddress);
	}
	
	/**
	 * Process variables common for all OpenWeatherMapRules
	 * 
	 * @param jsonObject
	 * @param values
	 */
	protected void processRuleCommonVariables(JSONObject jsonObject, Map<String, String> values) {

		String appId = values.get(ContextKeys.APP_ID);
		int cacheTimeout = GetterUtil.getInteger(values.get(ContextKeys.CACHE_TIMEOUT));
		int connectionTimeout = GetterUtil.getInteger(values.get(ContextKeys.CONNECTION_TIMEOUT));
		String testIpAddress = values.get(ContextKeys.TEST_IP_ADDRESS);

		jsonObject.put(ContextKeys.APP_ID, appId);
		jsonObject.put(ContextKeys.CACHE_TIMEOUT, cacheTimeout);
		jsonObject.put(ContextKeys.CONNECTION_TIMEOUT, connectionTimeout);
		jsonObject.put(ContextKeys.TEST_IP_ADDRESS, testIpAddress);

	}	
	// Default cache timeout in seconds

	public static final int DEFAULT_CACHE_TIMEOUT = 1800;

	// Default connection timeout in milliseconds

	public static final int DEFAULT_CONNECTION_TIMEOUT = 200;
	
	private static final Log _log = LogFactoryUtil.getLog(BaseOpenWeatherMapRule.class);
}