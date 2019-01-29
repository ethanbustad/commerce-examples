package com.liferay.commerce.product.sample;

import com.liferay.commerce.product.catalog.rule.CPRuleTypeJSPContributor;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = "commerce.product.rule.type.jsp.contributor.key=" + NameMatcherCPRuleTypeImpl.KEY,
	service = CPRuleTypeJSPContributor.class
)
public class NameMatcherCPRuleTypeJSPContributor
	implements CPRuleTypeJSPContributor {

	@Override
	public void render(
			long cpRuleId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/contributor/name_matcher.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.sample)"
	)
	private ServletContext _servletContext;

}