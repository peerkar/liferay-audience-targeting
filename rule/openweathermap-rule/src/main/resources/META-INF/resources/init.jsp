<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@page import="fi.soveltia.content.targeting.rule.openweathermap.constants.ResourceRequestKeys"%>
<%@page import="fi.soveltia.content.targeting.rule.openweathermap.display.context.OpenWeatherMapRuleDisplayContext"%>
<%@page import="fi.soveltia.content.targeting.rule.openweathermap.constants.ContextKeys"%>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<% 
	OpenWeatherMapRuleDisplayContext openWeatherMapRuleDisplayContext = new OpenWeatherMapRuleDisplayContext(liferayPortletRequest, liferayPortletResponse);
%>
