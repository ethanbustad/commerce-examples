package com.liferay.commerce.product.sample;

import com.liferay.commerce.product.catalog.rule.CPRuleType;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPRule;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
		"commerce.product.rule.type.key=" + NameMatcherCPRuleTypeImpl.KEY,
		"commerce.product.rule.type.order:Integer=100"
	},
	service = CPRuleType.class
)
public class NameMatcherCPRuleTypeImpl implements CPRuleType {

	public static final String KEY = "name-matching";

	/**
	 * Answers the question: What needs to be added to the search indexer document in order
	 * to properly find the definitions I want?
	 */
	@Override
	public void contributeDocument(Document document, CPDefinition cpDefinition)
		throws PortalException {

		// nothing to do here -- the cpDefinition name is already indexed
	}

	/**
	 * Answers the question: Which CPRuleType implementation is this?
	 */
	@Override
	public String getKey() {
		return KEY;
	}

	/**
	 * Answers the question: What label should we provide on the frontend for users to select
	 * this type?
	 */
	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		// gets the lang value for the given key _from the current module_
		return LanguageUtil.get(resourceBundle, KEY);
	}

	/**
	 * Answers the question: What supplementary information or configuration do we need to
	 * store on the rule to make it work properly once we know that the rule is
	 * of this type?
	 */
	@Override
	public UnicodeProperties getTypeSettingsProperties(
		HttpServletRequest httpServletRequest) {

		UnicodeProperties typeSettingsProperties = new UnicodeProperties(true);

		String nameSubstring = ParamUtil.getString(
			httpServletRequest, "nameSubstring");

		typeSettingsProperties.put("nameSubstring", nameSubstring);

		return typeSettingsProperties;
	}

	/**
	 * Answers the question: Does the given definition (which represents a product) match the
	 * criteria specified by the given rule (which is of this type)?
	 */
	@Override
	public boolean isSatisfied(CPDefinition cpDefinition, CPRule cpRule)
		throws PortalException {

		UnicodeProperties typeSettingsProperties =
			cpRule.getTypeSettingsProperties();

		Map<Locale, String> nameMap = cpDefinition.getNameMap();

		for (String name : nameMap.values()) {
			if (name.contains(typeSettingsProperties.get("nameSubstring"))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Answers the question: What needs to be added to a search query in order to match
	 * only products specified by the given rule (which is of this type)?
	 */
	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter booleanFilter, CPRule cpRule)
		throws PortalException {

		UnicodeProperties typeSettingsProperties =
			cpRule.getTypeSettingsProperties();

		booleanFilter.addRequiredTerm(
			Field.NAME, typeSettingsProperties.get("nameSubstring"));
	}

	/**
	 * Answers the question: What business logic needs to happen when the given rule is saved
	 * (e.g. if rule configuration data is stored anywhere other than the
	 * typeSettingsProperties field)?
	 */
	@Override
	public void update(CPRule cpRule, HttpServletRequest httpServletRequest) {
		// nothing to do here -- all configuration is persisted in
		// cpRule.typeSettingsProperties
	}

}