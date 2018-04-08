<%@ include file="init.jsp" %>

<%
	// Have to use extra namespacing to avoid id collisions
	
	String rulePrefix = "weather";
%>

<%@ include file="common_fields.jsp" %>

<aui:select label="" name="weather">

	<% 
		for (String weather : openWeatherMapRuleDisplayContext.getWeatherOptions()) {
			%>
			<aui:option label="<%= weather %>" selected="<%= weather.equals(openWeatherMapRuleDisplayContext.getWeather()) %>" value="<%= weather %>" />
			<%
		}
	%>

</aui:select>

<aui:button-row>
	<aui:button name="weatherTest" value="test" />
</aui:button-row>

<div class="alert alert-info hide" id="<portlet:namespace />weatherTestResultsWrapper">
	<div id="<portlet:namespace />weatherTestResults"></div>
</div>

<portlet:resourceURL var="weatherTestURL" id="<%=ResourceRequestKeys.WEATHER_RULE_TEST %>" />

<aui:script use="aui-base,aui-io"> 
	
	AUI().ready(function(A) 
		{
			A.one('#<portlet:namespace />weatherTest').on('click', function() 
				{
					new A.io.request('<%=weatherTestURL.toString() %>', {   
						data: {
							<portlet:namespace /><%= ContextKeys.APP_ID %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.APP_ID %>').val(),
							<portlet:namespace /><%= ContextKeys.CACHE_TIMEOUT %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.CACHE_TIMEOUT %>').val(),
							<portlet:namespace /><%= ContextKeys.CONNECTION_TIMEOUT %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.CONNECTION_TIMEOUT %>').val(),
							<portlet:namespace /><%= ContextKeys.TEST_IP_ADDRESS %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.TEST_IP_ADDRESS %>').val(),
							<portlet:namespace /><%= ContextKeys.WEATHER %>: A.one('#<portlet:namespace /><%=ContextKeys.WEATHER %>').val()
						},
						cache: false,
						dataType : 'json',		
						on: {
							success: function (event, id, obj) {	
								
								var html = '';

								if (obj.responseText == null || obj.responseText.length == 0) {
									html  = '<liferay-ui:message key="empty-result" />';
									
								} else {
									
									var response = A.JSON.parse(obj.responseText);
									
									html += '<p><strong><liferay-ui:message key="test-result" />:</strong></p>';
									html += '<p>' + JSON.stringify(response.result) + '</p>';

									html += '<p><strong><liferay-ui:message key="response-data" />:</strong></p>';
									html += '<p>' + JSON.stringify(response.json) + '</p>';

									if (response.messages != null) {
										html += '<p><strong><liferay-ui:message key="messages" />:</strong></p>';
										html += '<p>' + JSON.stringify(response.messages) + '</p>';
									}
								}
																	
								A.one('#<portlet:namespace />weatherTestResults').html(html);
								
								A.one('#<portlet:namespace />weatherTestResultsWrapper').show();
							},
							failure: function () {	
								alert('<liferay-ui:message key="test-failed" />');
							}		
						}
					});				
				}
			);
		}	
	);

</aui:script>