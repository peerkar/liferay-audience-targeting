package fi.soveltia.content.targeting.rule.categories;

import com.liferay.content.targeting.api.model.BaseRuleCategory;
import com.liferay.content.targeting.api.model.RuleCategory;

import org.osgi.service.component.annotations.Component;

/**
 * Generic rule category
 * 
 * @author peerkar
 *
 */
@Component(
		immediate = true,
		property = { 
				"rule.category.order:Integer=600" 
		},
		service = RuleCategory.class
)
public class GenericRuleCategory extends BaseRuleCategory {

	public static final String KEY = "generic";

	@Override
	public String getCategoryKey() { 
		return KEY;
	}

	@Override
	public String getIcon() {
		return "icon-ok-sign";
	}

}
 