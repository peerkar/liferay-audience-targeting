package fi.soveltia.content.targeting.rule.openweathermap.net;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

import java.io.IOException;

/**
 * Hystrix command for getting weather
 * 
 * @author peerkar
 *
 */
public class GetWeatherCommand extends HystrixCommand<String> {

	public static final HystrixCommandGroupKey GROUP_KEY = HystrixCommandGroupKey.Factory.asKey("OpenWeatherMapRuleGroup");
	public static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("GetWeatherCommand");
	public static final HystrixThreadPoolKey THREAD_POOL_KEY = HystrixThreadPoolKey.Factory.asKey("GetWeatherCommand");
	
	public GetWeatherCommand(String url, int timeOut) {
        super(Setter
        		.withGroupKey(GROUP_KEY)
                .andCommandKey(COMMAND_KEY)
                .andThreadPoolKey(THREAD_POOL_KEY));
		
        ConfigurationManager.getConfigInstance().setProperty(
                "hystrix.command." + COMMAND_KEY +  ".execution.isolation.thread.timeoutInMilliseconds", timeOut);
	
		_url = url;

		_log.debug("Setting Hystrix command timeout to " + timeOut + " milliseconds.");
	}
	
	@Override
	protected String run() throws IOException {
		return  HttpUtil.URLtoString(_url);
	}
 
	private String _url;
	
	private static final Log _log = LogFactoryUtil.getLog(GetWeatherCommand.class);	
}
