package fi.soveltia.content.targeting.rule.openweathermap.test;

import com.liferay.content.targeting.util.PortletKeys;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.content.targeting.rule.openweathermap.BaseOpenWeatherMapRule;
import fi.soveltia.content.targeting.rule.openweathermap.constants.ContextKeys;
import fi.soveltia.content.targeting.rule.openweathermap.constants.ResourceRequestKeys;
import fi.soveltia.content.targeting.rule.openweathermap.provider.OpenWeatherMapProvider;

/**
 * Temperature rule test
 * 
 * @author peerkar
 *
 */
@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + PortletKeys.CT_ADMIN,
				"mvc.command.name=" + ResourceRequestKeys.TEMPERATURE_RULE_TEST
		},
		service = MVCResourceCommand.class
)
public class TemperatureRuleTestCommand  implements MVCResourceCommand {

		@Override
		public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {
			
			String appId = ParamUtil.getString(resourceRequest, ContextKeys.APP_ID);
			int cacheTimeout = 0;
			int connectionTimeout = ParamUtil.getInteger(resourceRequest, ContextKeys.CONNECTION_TIMEOUT, BaseOpenWeatherMapRule.DEFAULT_CONNECTION_TIMEOUT);

			// Not checking validity as this is a demo

			String ipAddress = ParamUtil.getString(resourceRequest, ContextKeys.TEST_IP_ADDRESS);

			int temperatureMax = ParamUtil.getInteger(resourceRequest, ContextKeys.TEMPERATURE_MAX);
			int temperatureMin = ParamUtil.getInteger(resourceRequest, ContextKeys.TEMPERATURE_MIN);

			JSONObject response = JSONFactoryUtil.createJSONObject();

			OpenWeatherMapProvider provider = new OpenWeatherMapProvider(appId, ipAddress, cacheTimeout, connectionTimeout);

			JSONObject userWeatherObject = provider.getUserWeatherObject();
			response.put("json", userWeatherObject);

			response.put("messages", provider.getEvents());

			Double userTemperature = provider.getTemperature();

			response.put("result", (userTemperature >= temperatureMin && userTemperature <= temperatureMax));
			
			try {
				PrintWriter writer = resourceResponse.getWriter();
				resourceResponse.setContentType("text/html");
				writer.print(response.toString());

			} catch (IOException e) {
				_log.error(e, e);
			}

			return false;
		}

		private static final Log _log = LogFactoryUtil.getLog(TemperatureRuleTestCommand.class);
}