/**
 * © 2018 Liferay, Inc. <https://liferay.com>
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.breccia.liferay.commerce.validation.configurationaction;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Ethan Bustad
 */
@ExtendedObjectClassDefinition(
	category = "catalog", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.breccia.liferay.commerce.validation.configurationaction.AddOnConfiguration",
	localization = "content/Language", name = "add-on-item-configuration"
)
public interface AddOnConfiguration {

	@Meta.AD(deflt = "USD", name = "currency-code", required = false)
	public String currencyCode();

	@Meta.AD(deflt = "add-on item", name = "tag-name", required = false)
	public String tagName();

	@Meta.AD(deflt = "25", name = "threshold", required = false)
	public int threshold();

}