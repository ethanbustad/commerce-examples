/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.breccia.liferay.commerce.validation;

import com.breccia.liferay.commerce.validation.configurationaction.AddOnConfiguration;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(
	configurationPid = "com.breccia.liferay.commerce.validation.configurationaction.AddOnConfiguration",
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

			// ideally this should be handled by localization, not just appended

			String money =
				_configuration.threshold() + " " +
					_configuration.currencyCode();

			return new CommerceOrderValidatorResult(
				commerceOrderItem.getCommerceOrderItemId(), false,
				"Orders with Add-On Items must add up to at least " + money +
					".");
		}

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			CPInstance cpInstance, int quantity)
		throws PortalException {

		return new CommerceOrderValidatorResult(true);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			AddOnConfiguration.class, properties);
	}

	private boolean _isAddOnItem(CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		if ((assetEntry != null) &&
			ArrayUtil.contains(
				assetEntry.getTagNames(), _configuration.tagName())) {

			return true;
		}

		return false;
	}

	private boolean _lessThanThreshold(CommerceOrder commerceOrder)
		throws PortalException {

		// get subtotal

		BigDecimal subtotal = BigDecimal.ZERO;

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			subtotal = subtotal.add(commerceOrderItem.getFinalPrice());
		}

		// get threshold

		BigDecimal threshold = BigDecimal.valueOf(_configuration.threshold());

		CommerceCurrency orderCurrency = commerceOrder.getCommerceCurrency();

		CommerceCurrency thresholdCurrency = null;

		try {
			thresholdCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					commerceOrder.getGroupId(), _configuration.currencyCode());
		}
		catch (NoSuchCurrencyException nsce) {
			_log.error(
				"The configured Add-On Item threshold currency is invalid",
				nsce);

			return false;
		}

		if (orderCurrency.getCommerceCurrencyId() !=
				thresholdCurrency.getCommerceCurrencyId()) {

			BigDecimal conversionRate = orderCurrency.getRate().divide(
				thresholdCurrency.getRate(), RoundingMode.HALF_EVEN);

			threshold = threshold.multiply(conversionRate);
		}

		// compare them

		if (subtotal.compareTo(threshold) < 0) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddOnValidatorImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private volatile AddOnConfiguration _configuration;

}