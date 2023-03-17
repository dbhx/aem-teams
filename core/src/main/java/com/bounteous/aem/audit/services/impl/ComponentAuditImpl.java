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

package com.bounteous.aem.audit.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.resource.LoginException;
import com.bounteous.aem.audit.models.ComponentAuditEntry;
import com.bounteous.aem.audit.services.ComponentAudit;
import com.bounteous.aem.audit.AuditConstants;
import org.apache.jackrabbit.commons.flat.TreeTraverser;

@Component(immediate = true, service = ComponentAudit.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Component Audit Service" })
public class ComponentAuditImpl implements ComponentAudit {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAuditImpl.class);

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private AdapterManager adapterManager;

	@Activate
	protected void activate() {

	}

	@Override
	public List<ComponentAuditEntry> getLatestEvents(String pagePath, String currentUser, String category) {
		final String AUDITPATH = AuditConstants.AUDIT_ROOT_PATH + "/" + category + pagePath;
		Map<String, Object> authenticationInfo = new HashMap<>();
		authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, AuditConstants.SUB_SERVICE_ID);

		List<ComponentAuditEntry> componentAuditEntries = new ArrayList<ComponentAuditEntry>();

		try {
			ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authenticationInfo);
			Resource parentResource = resolver.getResource(AUDITPATH);
			if (parentResource != null) {
				Node parentNode = parentResource.adaptTo(Node.class);
				Iterator<Node> auditNodes = new TreeTraverser(parentNode).iterator();
				while (auditNodes.hasNext()) {
					Node auditNode = auditNodes.next();
					if(auditNode.getProperty("jcr:primaryType").getString().equals("cq:AuditEvent")) {
						String auditNodeName = auditNode.getName();
						String path = auditNode.getProperty("path").getString();
						String type = auditNode.getProperty("type").getString();
						String userId = auditNode.getProperty("userId").getString();
						String componentName = auditNode.getProperty("componentName").getString();
						String componentType = auditNode.getProperty("componentType").getString();
						ComponentAuditEntry componentAuditEntry = new ComponentAuditEntry();
						componentAuditEntry.setCategory(category);
						Calendar time = auditNode.getProperty("cq:time").getDate();
						componentAuditEntry.setDate(time.getTime());
						componentAuditEntry.setPath(path);
						componentAuditEntry.setType(type);
						componentAuditEntry.setUserId(userId);
						componentAuditEntry.setComponentName(componentName);
						componentAuditEntry.setComponentType(componentType);
						componentAuditEntry.setUid(auditNodeName);
						if(auditNode.hasProperty("propertyAdded")) {
							componentAuditEntry.setPropertyAdded(auditNode.getProperty("propertyAdded").getString());
							componentAuditEntry.setHasBefore(false);
						}
						if (auditNode.hasProperty("propertyAddedValue")) {
							componentAuditEntry.setPropertyAddedValue(auditNode.getProperty("propertyAddedValue").getString());
						}
						if(auditNode.hasProperty("propertyChanged")) {
							componentAuditEntry.setPropertyChanged(auditNode.getProperty("propertyChanged").getString());
							if(auditNode.getProperty("propertyChanged").getString().equals("text")) {
							}
							
						}
						if (auditNode.hasProperty("propertyChangedValue")) {
							LOGGER.debug("propertyChangedValue :{}",auditNode.getProperty("propertyChangedValue").getString());
							componentAuditEntry.setPropertyChangedValue(auditNode.getProperty("propertyChangedValue").getString());
						}

					componentAuditEntries.add(componentAuditEntry);
					}
					
				}
			}

		} catch (LoginException e) {
			LOGGER.error(e.getMessage());

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		LOGGER.debug("returning results: {}", componentAuditEntries.size());
		return componentAuditEntries;
	}
	

}
