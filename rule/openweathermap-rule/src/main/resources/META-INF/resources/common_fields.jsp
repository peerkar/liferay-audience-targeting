<div class="alert alert-info">
	<liferay-ui:message key="get-openweathermap-app-id" />
</div>	

<div class="flex-container">

	<div class="flex-item-full">
		<aui:input 
			id="<%=rulePrefix + ContextKeys.APP_ID %>" 
			label="application-id" 
			name="<%=ContextKeys.APP_ID %>" 
			type="text" 
			value="<%= openWeatherMapRuleDisplayContext.getAppId() %>">
		
			<aui:validator name="required"></aui:validator>
		
		</aui:input>	
	</div>

	<div class="flex-item-full">
		<aui:input 
			id="<%=rulePrefix + ContextKeys.CACHE_TIMEOUT %>" 
			label="cache-timeout" 
			name="<%=ContextKeys.CACHE_TIMEOUT %>" 
			type="text" 
			value="<%= openWeatherMapRuleDisplayContext.getCacheTimeout() %>">
	
			<aui:validator name="required"></aui:validator>
			<aui:validator name="digits"></aui:validator>
		</aui:input>		
	</div>

	<div class="flex-item-full">
		<aui:input 
			id="<%=rulePrefix + ContextKeys.CONNECTION_TIMEOUT %>" 
			label="connection-timeout" 
			name="<%=ContextKeys.CONNECTION_TIMEOUT %>" 
			type="text" 
			value="<%= openWeatherMapRuleDisplayContext.getConnectionTimeout() %>">
			
			<aui:validator name="required"></aui:validator>
			<aui:validator name="digits"></aui:validator>
		</aui:input>		
	</div>

	<div class="flex-item-full">
		<aui:input 
			id="<%=rulePrefix + ContextKeys.TEST_IP_ADDRESS %>" 
			label="test-ip-address" 
			name="<%=ContextKeys.TEST_IP_ADDRESS %>" 
			type="text" 
			value="<%= openWeatherMapRuleDisplayContext.getTestIpAddress() %>"
		/>
	</div>

</div>
