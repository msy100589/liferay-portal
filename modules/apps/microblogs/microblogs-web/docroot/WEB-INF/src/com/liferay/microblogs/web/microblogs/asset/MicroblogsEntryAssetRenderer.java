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

package com.liferay.microblogs.web.microblogs.asset;

import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.permission.MicroblogsEntryPermission;
import com.liferay.microblogs.util.WebKeys;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.asset.model.BaseAssetRenderer;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

/**
 * @author Matthew Kong
 */
public class MicroblogsEntryAssetRenderer extends BaseAssetRenderer {

	public MicroblogsEntryAssetRenderer(MicroblogsEntry entry) {
		_entry = entry;
	}

	@Override
	public String getClassName() {
		return MicroblogsEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _entry.getMicroblogsEntryId();
	}

	@Override
	public long getGroupId() {
		try {
			Group group = GroupLocalServiceUtil.getCompanyGroup(
				_entry.getCompanyId());

			return group.getGroupId();
		}
		catch (Exception e) {
		}

		return 0;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _entry.getContent();
	}

	@Override
	public String getTitle(Locale locale) {
		return _entry.getContent();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getUser();

			long portletPlid = PortalUtil.getPlidFromPortletId(
				user.getGroupId(), true, "1_WAR_microblogsportlet");

			PortletURL portletURL = PortletURLFactoryUtil.create(
				liferayPortletRequest, "1_WAR_microblogsportlet", portletPlid,
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter("mvcPath", "/microblogs/view.jsp");

			long microblogsEntryId = _entry.getMicroblogsEntryId();

			if (_entry.getParentMicroblogsEntryId() > 0) {
				microblogsEntryId =_entry.getParentMicroblogsEntryId();
			}

			portletURL.setParameter(
				"parentMicroblogsEntryId", String.valueOf(microblogsEntryId));

			return portletURL.toString();
		}
		catch (Exception e) {
		}

		return null;
	}

	@Override
	public long getUserId() {
		return _entry.getUserId();
	}

	@Override
	public String getUserName() {
		return _entry.getUserName();
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		try {
			return MicroblogsEntryPermission.contains(
				permissionChecker, _entry, ActionKeys.VIEW);
		}
		catch (Exception e) {
		}

		return false;
	}

	@Override
	public String render(
			PortletRequest portletRequest, PortletResponse portletResponse,
			String template)
		throws Exception {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			portletRequest.setAttribute(WebKeys.MICROBLOGS_ENTRY, _entry);

			return "/microblogs/asset/" + template + ".jsp";
		}
		else {
			return null;
		}
	}

	private final MicroblogsEntry _entry;

}