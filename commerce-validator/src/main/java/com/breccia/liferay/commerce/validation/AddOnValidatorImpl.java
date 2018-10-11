package com.breccia.liferay.commerce.validation;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.math.BigDecimal;
import java.util.List;

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

	public static final String TAG_NAME = "add-on item";

	public static final BigDecimal THRESHOLD = BigDecimal.valueOf(25);

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CommerceOrderValidatorResult validate(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (_isAddOnItem(commerceOrderItem) &&
			_lessThanThreshold(commerceOrderItem.getCommerceOrder())) {

			return new CommerceOrderValidatorResult(
				commerceOrderItem.getCommerceOrderItemId(), false,
				"Orders with Add-On Items must add up to at least " +
					THRESHOLD + " dollars");
		}

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			CPInstance cpInstance, int quantity)
		throws PortalException {

		return new CommerceOrderValidatorResult(true);
	}

	private boolean _isAddOnItem(CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		if ((assetEntry != null) &&
			ArrayUtil.contains(assetEntry.getTagNames(), TAG_NAME)) {

			return true;
		}

		return false;
	}

	private boolean _lessThanThreshold(CommerceOrder commerceOrder) {
		BigDecimal subtotal = BigDecimal.ZERO;

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			subtotal = subtotal.add(commerceOrderItem.getFinalPrice());
		}

		return subtotal.compareTo(THRESHOLD) < 0;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}