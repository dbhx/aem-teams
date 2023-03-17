/*
 * ***********************************************************************
 * MIT License
 *
 * Copyright (c) 2023 Diana Henrickson (diana.henrickson@bounteous.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ***********************************************************************
 */

package com.bounteous.aem.teams.services.impl;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.bounteous.aem.teams.AemTeamsConstants.PN_EMAIL;
import static com.bounteous.aem.teams.AemTeamsConstants.PROFILE;
import static com.bounteous.aem.teams.utils.AemTeamsUtils.getUserEmail;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Map.Entry;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import com.bounteous.aem.teams.services.ComponentHistoryService;
import com.bounteous.aem.teams.models.Audit;
import com.bounteous.aem.teams.models.AuditUser;
import com.bounteous.aem.teams.models.AuditData;
import com.bounteous.aem.teams.models.AuditHistory;
import com.bounteous.aem.teams.AemTeamsConstants;
import com.bounteous.aem.teams.services.TeamsConfig;
import com.bounteous.aem.audit.models.ComponentAuditEntry;
import com.bounteous.aem.audit.services.ComponentAudit;

@Component(immediate = true, service = ComponentHistoryService.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Component History Service" })
public class ComponentHistoryServiceImpl implements ComponentHistoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentHistoryServiceImpl.class);

	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm");

	@Reference
	private TeamsConfig teamsConfig;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private ComponentAudit componentAudit;

	@Activate
	protected void activate() {

	}

	@Override
	public AuditData getHistoryCollection(String requestUri, String currentUser) {
		String pagePath = setPagePath(requestUri);
		Map<String, Object> authenticationInfo = new HashMap<>();
		authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, AemTeamsConstants.SUB_SERVICE_ID);
		AuditData auditData = null;

		List<Audit> userHistoryList = new ArrayList<>();
		List<AuditUser> auditUserList = new ArrayList<>();
		try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authenticationInfo)) {
			UserManager userManager = resolver.adaptTo(UserManager.class);
			String pageTitle = Objects.requireNonNull(resolver.getResource(pagePath)).getName();		
			auditData = setAuditData(pageTitle);
			auditData.setPagePath(pagePath);

			String category = "com.day.cq.component";
			List<ComponentAuditEntry> componentAuditEntries = componentAudit.getLatestEvents(pagePath, currentUser,
					category);
			MultiValuedMap<String, ComponentAuditEntry> userAuditMap = new ArrayListValuedHashMap<>();
			for (ComponentAuditEntry componentAuditEntry : componentAuditEntries) {
				userAuditMap.put(componentAuditEntry.getUserId(), componentAuditEntry);
			}
			Iterator<Map.Entry<String, ComponentAuditEntry>> userAuditMapIt = userAuditMap.entries().iterator();
			List<String> usersList = new ArrayList<>();
			while (userAuditMapIt.hasNext()) {
				Entry<String, ComponentAuditEntry> auditMapEntry = userAuditMapIt.next();
				String userId = auditMapEntry.getKey();
				if (!StringUtils.equalsIgnoreCase(userId, currentUser) && !usersList.contains(userId)
						&& !StringUtils.equalsIgnoreCase(userId, "version-manager-service")) {
					usersList.add(userId);
					List<AuditHistory> historyList = new ArrayList<>();
					Collection<ComponentAuditEntry> userAuditEntries = userAuditMap.get(userId);
					for (ComponentAuditEntry componentAuditEntry : userAuditEntries) {
						AuditHistory history = setAuditHistory(userId, componentAuditEntry, pagePath, resolver,
								componentAuditEntries);
						history.setPersists(itemPersists(history.getItemContentPath(), resolver));
						historyList.add(history);
					}
					Audit audit = new Audit();
					historyList.sort(Comparator.comparing(AuditHistory::getDate));
					audit.setHistory(historyList);
					AuditUser auditUser;
					String currentUserEmail = getUserEmail(currentUser, Objects.requireNonNull(userManager), resolver);
					
					if (StringUtils.isNotBlank(currentUserEmail)) {
						auditData.setIsTeamsGroupValid(true);
					} else {
						auditData.setIsTeamsGroupValid(false);
					}
					auditUser = getAuditUser(userId, userManager, resolver, currentUserEmail);
					audit.setAuditUser(auditUser);
					auditUserList.add(auditUser);
					Comparator<AuditHistory> latestUpdateComparator = Comparator.comparing(AuditHistory::getModified);
					AuditHistory latestUpdate = historyList.stream().max(latestUpdateComparator).get();
					Date date = DATEFORMAT.parse(latestUpdate.getModified());
					SimpleDateFormat latestUpdateFormat = new SimpleDateFormat("MM-dd-yyyy");
					String latestUpdateFormatted = latestUpdateFormat.format(date);
					audit.setLastModified(latestUpdateFormatted);
					audit.setPageEditors(auditUserList);
					audit.setDate(date);
					userHistoryList.add(audit);
				}
			}
			userHistoryList.sort(Comparator.comparing(Audit::getDate));
			auditData.setAudits(userHistoryList);
			auditData.setShowGroupChatLink(userHistoryList.size() > 1);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());

		}
		return auditData;
	}

	private String setPagePath(String requestUri) {
		String pagePath;
		pagePath = requestUri.substring(requestUri.lastIndexOf("/content")).replace(".html", StringUtils.EMPTY);
		return pagePath;
	}

	private AuditData setAuditData(String pageTitle) {
		AuditData auditData = new AuditData();
		auditData.setIsTeamsOn(teamsConfig.getTeamsEnabled());
		auditData.setTeamsTenantId(teamsConfig.getTeamsTenantId());
		return auditData;
	}

	private AuditHistory setAuditHistory(String userId, ComponentAuditEntry componentAuditEntry, String pagePath,
			ResourceResolver resolver, List<ComponentAuditEntry> componentAuditEntries) throws ValueFormatException, PathNotFoundException, RepositoryException {
		AuditHistory auditHistory = new AuditHistory();
		auditHistory.setUserId(userId);
		auditHistory.setDate(componentAuditEntry.getDate());
		auditHistory.setModified(DATEFORMAT.format(componentAuditEntry.getDate()));
		auditHistory.setItemContentPath(componentAuditEntry.getPath());
		String changeType = componentAuditEntry.getType();
		String action = "";
		switch (changeType) {
        case "ComponentAdded":   
        	action = "created";
            break;
        case "ComponentRemoved":  
            action = "deleted";
            break;
        case "ComponentUpdated":  
        	action = "updated";
            break;
        case "PropertyAdded":  
        	action = "updated";
        	String propertyAddedPath = componentAuditEntry.getPath().substring(0, componentAuditEntry.getPath().lastIndexOf("/"));
        	auditHistory.setItemContentPath(propertyAddedPath);
            break;
        case "PropertyChanged":  
        	action = "updated";
        	String propertyChangedPath = componentAuditEntry.getPath().substring(0, componentAuditEntry.getPath().lastIndexOf("/"));
        	auditHistory.setItemContentPath(propertyChangedPath);       	
            break;
        }
		auditHistory.setUid(componentAuditEntry.getUid());
		auditHistory.setAction(action);
		auditHistory.setIsActivate(false);
		boolean created = false;
		
		if(changeType.equals("ComponentAdded")) {
			created = true;
		}
		auditHistory.setComponentName(componentAuditEntry.getComponentName().toUpperCase() + " Component");
		auditHistory.setComponentType(componentAuditEntry.getComponentType());
		String relativeItemPath = componentAuditEntry.getPath();
		if(!componentAuditEntry.getPath().endsWith("jcr:content")) {
			relativeItemPath = componentAuditEntry.getPath().split("jcr:content")[1];
		}
		auditHistory.setItem(relativeItemPath);
		if(changeType.equals("PropertyAdded")) {
			auditHistory.setFieldName(componentAuditEntry.getPropertyAdded());
			auditHistory.setFieldValue(componentAuditEntry.getPropertyAddedValue());
		}
		if(changeType.equals("PropertyChanged")) {
			auditHistory.setFieldName(componentAuditEntry.getPropertyChanged());
			auditHistory.setFieldValue(componentAuditEntry.getPropertyChangedValue());
		}
		auditHistory.setCreated(created);
	
		return auditHistory;
	}

	private boolean itemPersists(String itemContentPath, ResourceResolver resolver) {
		return resolver.getResource(itemContentPath) != null;
	}

	private AuditUser getAuditUser(String userId, UserManager userManager, ResourceResolver resolver,
			String currentUserEmail) {
		AuditUser auditUser = new AuditUser();
		try {
			User user = (User) userManager.getAuthorizable(userId);
			Resource profile = resolver.getResource(Objects.requireNonNull(user).getPath() + PROFILE);
			if (profile != null) {
				ValueMap profileValues = profile.getValueMap();
				String email = profileValues.get(PN_EMAIL, StringUtils.EMPTY);
				String firstName = profileValues.get("givenName", StringUtils.EMPTY);
				String lastName = profileValues.get("familyName", StringUtils.EMPTY);
				auditUser.setEmail(email);
				auditUser.setFirstName(firstName);
				auditUser.setLastName(lastName);
				auditUser.setUserId(userId);
				auditUser.setTeamsId(email);
				auditUser.setIsTeamsIdValid(false);

				if (StringUtils.isNotBlank(auditUser.getEmail()) && StringUtils.isNotBlank(currentUserEmail)) {
					String currentUserDomain = currentUserEmail.split("@")[1];
					String auditUserDomain = auditUser.getEmail().split("@")[1];
					if (currentUserDomain.equals(auditUserDomain)) {
						auditUser.setIsTeamsIdValid(true);
					}
				}
			}
		} catch (RepositoryException e) {
			LOGGER.error("Exception while auditing the user: {}", e.getMessage());
		}
		return auditUser;
	}
	
	
	
}

