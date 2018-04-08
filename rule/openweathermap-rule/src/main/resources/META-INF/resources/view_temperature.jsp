<%@ include file="init.jsp" %>

<%
	// Have to use extra namespacing to avoid id collisions
	
	String rulePrefix = "temperature";
%>

<%@ include file="common_fields.jsp" %>

<div class="flex-container">

	<div class="col-md-6">
		<aui:input 
			cssClass="slider-input" 
			inlineField="<%= true %>" 
			maxlength="3" 
			name="<%=ContextKeys.TEMPERATURE_MIN %>" 
			size="3" 
			value="<%= openWeatherMapRuleDisplayContext.getTemperatureMin() %>" 
		/>
	</div>
	
	<div class="col-md-6">
		<aui:input 
			cssClass="slider-input" 
			inlineField="<%= true %>" 
			maxlength="3" 
			name="<%=ContextKeys.TEMPERATURE_MAX %>" 
			size="3" 
			value="<%= openWeatherMapRuleDisplayContext.getTemperatureMax() %>" 
		/>
	</div>
</div>

<aui:button-row>
	<aui:button name="temperatureTest" value="test" />
</aui:button-row>

<div class="alert alert-info hide" id="<portlet:namespace />temperatureTestResultsWrapper">
	<div id="<portlet:namespace />temperatureTestResults"></div>
</div>

<aui:script>

	/* Based on liferay-input-slider */
	
	AUI.add(
		'weather-input-slider',
	
		function(A) {
			var TPL_INVALID_RANGE = '<div class="has-error hide" id="{invalidRangeId}">' +
									'<p class="help-block">{invalidRangeMessage}</p></div>';
	
			var TPL_SLIDER = '<span class="slider-holder"></span>';
	
			var InputSlider = A.Component.create(
				{
					ATTRS: {
	
						form: {},
	
						inputNode: {
							setter: A.one
						},
	
						invalidRangeId: {
							validator: A.Lang.isString,
							value: ''
						},
	
						invalidRangeMessage: {
							validator: A.Lang.isString,
							value: ''
						},
	
						min: {
							value: -50
						},

						max: {
							value: 50
						},

						rangeCheckNode: {
							setter: A.one
						},
	
						sliderNode: {
							setter: A.one
						}
					},
	
					EXTENDS: A.Slider,
	
					NAME: 'weatherinputslider',
	
					prototype: {
						initializer: function(config) {
							
							var instance = this;
	
							var inputNode = instance.get('inputNode');
	
							var invalidRangeMessage = instance.get('invalidRangeMessage');
	
							var sliderNode = instance.get('sliderNode');
	
							var invalidRangeId = A.guid();
	
							if (!sliderNode) {
								sliderNode = A.Node.create(TPL_SLIDER);
	
								inputNode.insert(sliderNode, 'after');
							}
	
							var invalidRangeNode = A.Lang.sub(
								TPL_INVALID_RANGE,
								{
									invalidRangeId: invalidRangeId,
									invalidRangeMessage: invalidRangeMessage
								}
							);
	
							var min = instance.get('min');
							var max = instance.get('max');
							
							sliderNode.insert(invalidRangeNode, 'after');
	
							instance.set('invalidRangeId', invalidRangeId);
	
							instance._slider = new A.Slider(
								{
									min: min,
									max: max,
									render: sliderNode,
									value: inputNode.val()
								}
							);
	
							instance._bindUISlider();
						},
	
						destructor: function() {
							var instance = this;
	
							(new A.EventHandle(instance._eventHandles)).detach();
						},
	
						_addFormValidatorError: function() {
							var instance = this;
	
							var invalidRangeId = instance.get('invalidRangeId');
	
							var form = instance.get('form');
	
							var formValidator = form.formValidator;
	
							formValidator.addFieldError(
								{
									get: function() {
										return invalidRangeId;
									}
								}
							);
						},
	
						_bindUISlider: function() {
							var instance = this;
	
							var inputNode = instance.get('inputNode');
	
							instance._eventHandles = [
								inputNode.on('input', A.debounce(instance._updateSlider, 200, instance)),
								instance._slider.after('valueChange', instance._updateInput, instance)
							];
	
							var rangeCheckNode = instance.get('rangeCheckNode');
	
							if (rangeCheckNode) {
								var form = instance.get('form');
	
								var formValidator = form.formValidator;
	
								instance._eventHandles.push(inputNode.on('change', instance._validateRange, instance)),
								instance._eventHandles.push(instance._slider.on('slideEnd', instance._validateRange, instance));
								instance._eventHandles.push(A.Do.before(instance._validateRange, formValidator, 'validate', instance));
							}
	
							instance._updateSlider();
						},
	
						_clearFormValidatorError: function() {
							var instance = this;
	
							var invalidRangeId = instance.get('invalidRangeId');
	
							var form = instance.get('form');
	
							var formValidator = form.formValidator;
	
							formValidator.clearFieldError(invalidRangeId);
						},
	
						_updateInput: function(event) {
							var instance = this;
	
							var inputNode = instance.get('inputNode');
	
							inputNode.val(event.newVal);
						},
	
						_updateSlider: function() {
							var instance = this;
	
							var inputNode = instance.get('inputNode');
	
							var value = parseInt(inputNode.val(), 10) || 0;

							instance._slider.set('value', value);
						},
	
						_validateRange: function() {
							var instance = this;
	
							var inputNode = instance.get('inputNode');
							var invalidRangeNode = A.one('#' + instance.get('invalidRangeId'));
							var rangeCheckNode = instance.get('rangeCheckNode');
	
							var inputNodeVal = parseInt(inputNode.val());
							var rangeCheckNodeVal = parseInt(rangeCheckNode.val());
							
							instance._clearFormValidatorError();
	
							invalidRangeNode.hide();
		
							if (rangeCheckNodeVal > inputNodeVal) {
								
								invalidRangeNode.show();
	
								instance._addFormValidatorError();
							}
						}
					}
				}
			);
	
			Liferay.WeatherInputSlider = InputSlider;
		},
		'',
		{
			requires: ['aui-base', 'aui-event-input', 'slider']
		}
	);

</aui:script>

<aui:script use="weather-input-slider">

	var minSlider = new Liferay.WeatherInputSlider(
		{
			inputNode: '#<portlet:namespace /><%=ContextKeys.TEMPERATURE_MIN %>'
		}
	);

	var maxSlider = new Liferay.WeatherInputSlider(
		{
			form: Liferay.Form.get('<portlet:namespace />fm'),
			inputNode: '#<portlet:namespace /><%=ContextKeys.TEMPERATURE_MAX %>',
			invalidRangeMessage: '<liferay-ui:message key="temperature-range-is-invalid" />',
			min: -50,
			max: 50,
			rangeCheckNode: '#<portlet:namespace /><%=ContextKeys.TEMPERATURE_MIN %>'
		}
	);
</aui:script>

<portlet:resourceURL var="temperatureTestURL" id="<%=ResourceRequestKeys.TEMPERATURE_RULE_TEST %>" />

<aui:script use="aui-base,aui-io"> 
	
	AUI().ready(function(A) 
		{
			A.one('#<portlet:namespace />temperatureTest').on('click', function() 
				{
					new A.io.request('<%=temperatureTestURL.toString() %>', {   
						data: {
							<portlet:namespace /><%= ContextKeys.APP_ID %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.APP_ID %>').val(),
							<portlet:namespace /><%= ContextKeys.CACHE_TIMEOUT %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.CACHE_TIMEOUT %>').val(),
							<portlet:namespace /><%= ContextKeys.CONNECTION_TIMEOUT %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.CONNECTION_TIMEOUT %>').val(),
							<portlet:namespace /><%= ContextKeys.TEST_IP_ADDRESS %>: A.one('#<portlet:namespace /><%=rulePrefix %><%=ContextKeys.TEST_IP_ADDRESS %>').val(),
							<portlet:namespace /><%= ContextKeys.TEMPERATURE_MAX %>: A.one('#<portlet:namespace /><%=ContextKeys.TEMPERATURE_MAX %>').val(),
							<portlet:namespace /><%= ContextKeys.TEMPERATURE_MIN %>: A.one('#<portlet:namespace /><%=ContextKeys.TEMPERATURE_MIN %>').val()
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
																	
								A.one('#<portlet:namespace />temperatureTestResults').html(html);
								
								A.one('#<portlet:namespace />temperatureTestResultsWrapper').show();
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
	