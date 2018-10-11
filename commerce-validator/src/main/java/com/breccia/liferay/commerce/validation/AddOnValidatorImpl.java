package com.breccia.liferay.commerce.validation;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"commerce.order.validator.key=" + AddOnValidatorImpl.KEY,
		"commerce.order.validator.priority:Integer=10"
	},
	service = CommerceOrderValidator.class
)
public class AddOnValidatorImpl implements CommerceOrderValidator {

	public static final String KEY = "add-on-item-validator";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CommerceOrderValidatorResult validate(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		// What validation should run during the checkout process?
	}

	@Override
	public CommerceOrderValidatorResult validate(
			CPInstance cpInstance, int quantity)
		throws PortalException {

		// What validation should run when an item is added?
	}

}
