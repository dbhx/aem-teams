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

package com.bounteous.aem.audit.listeners;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.commons.jcr.ObservationUtil;
import org.osgi.service.component.ComponentContext;
import javax.jcr.observation.EventIterator;
import com.bounteous.aem.audit.ComponentAuditConfig;

@Component(immediate = true, service = EventListener.class)

public class ContentFragmentEventListener implements EventListener {

	Logger LOGGER = LoggerFactory.getLogger(ContentFragmentEventListener.class);

	private Session session;

	@Reference
	private SlingRepository repository;

	private ResourceResolver resolver;

	@Reference
	ResourceResolverFactory resolverFactory;

	@Reference
	ComponentAuditConfig componentAuditConfig;

	static final String AUDIT_NODE_PATH = "/var/audit/com.day.cq.component";
	static final String AUDIT_CATEGORY = "com.day.cq.component";

	@Activate
	public void activate(ComponentContext context) throws Exception {
		Map<String, Object> authenticationInfo = new HashMap<String, Object>();
		authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, "component-audit-service");
		resolver = resolverFactory.getServiceResourceResolver(authenticationInfo);
		session = (Session) resolver.adaptTo(Session.class);
		String[] nodeTypes = { "dam:Asset", "nt:unstructured" };
		session.getWorkspace().getObservationManager().addEventListener(this,
				Event.NODE_ADDED | Event.PROPERTY_CHANGED, "/content/dam", true, null, nodeTypes,
				false);

	}

	@Deactivate
	public void deactivate() {
		if (session != null) {
			session.logout();
		}
	}

	public void onEvent(EventIterator eventIterator) {
		try {
			List<String> IGNORED_PROPERTY_PATTERNS = componentAuditConfig.getIgnoredPropertyPatternsList();
			List<String> IGNORED_PROPERTIES = componentAuditConfig.getIgnoredPropertiesList();
			while (eventIterator.hasNext()) {
				Event event = eventIterator.nextEvent();
				if (ObservationUtil.isExternal(event)) {
					return;
				}
				int eventType = event.getType();
				String eventPath = event.getPath();
				if (event.getUserID().equals("reference-processing-service")) {
					return;
				}
				switch (eventType) {
				case Event.NODE_ADDED:
					createAuditNodeForCreated(eventPath, event.getDate(), event.getUserID(), "ComponentAdded");
					break;
				case Event.PROPERTY_CHANGED:
					createAuditNodeForPropertyChanged(IGNORED_PROPERTY_PATTERNS, IGNORED_PROPERTIES, eventPath,
							event.getDate(), event.getUserID(), "PropertyChanged");
					break;
				}

			}

		} catch (RepositoryException e) {
			LOGGER.error("Error while handling events", e.getMessage());
		}
	}

	private void createAuditNodeForCreated(String nodePath, Long date, String userId, String action) {
		if (nodePath.endsWith("jcr:content")) {
			String auditNodeName = UUID.randomUUID().toString();
			Date time = new Date(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			

			Resource eventRes = resolver.getResource(nodePath);
			if (eventRes != null) {
				String cfTitle = eventRes.getValueMap().get("jcr:title", "");
				String cfName = eventRes.getValueMap().get("cq-name", "");
				String name = "";
				if (!cfName.equals("")) {
					name = cfName;
				} else {
					name = cfTitle;
				}
			}
			String absolutePath = AUDIT_NODE_PATH + nodePath.substring(0, nodePath.lastIndexOf("/jcr:content")) + "/"
					+ auditNodeName;
			try {

				Node auditNode = JcrUtil.createPath(absolutePath, false, "sling:Folder", "cq:AuditEvent", session,
						false);
				auditNode.setProperty("path", nodePath);
				auditNode.setProperty("cq:path", nodePath);
				auditNode.setProperty("type", action);
				auditNode.setProperty("cq:type", action);
				auditNode.setProperty("cq:category", AUDIT_CATEGORY);
				auditNode.setProperty("cq:time", calendar);
				auditNode.setProperty("cq:userid", userId);
				auditNode.setProperty("userId", userId);
				auditNode.setProperty("componentName", "master");
				auditNode.setProperty("componentType", "contentFragment");

				session.save();
			} catch (RepositoryException e) {
				LOGGER.error("Failed to create cf audit node : {}", e.getMessage());
			}
		}

	}

	private void createAuditNodeForPropertyChanged(List<String> IGNORED_PROPERTY_PATTERNS,
			List<String> IGNORED_PROPERTIES, String nodePath, Long date, String userId, String action) {

		String[] pathParticles = nodePath.split("/");
		int lengthOfParticles = pathParticles.length;
		String componentName = pathParticles[lengthOfParticles - 2];
		String auditNodeName = UUID.randomUUID().toString();
		Date time = new Date(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);

		try {
			String propName = nodePath.substring(nodePath.lastIndexOf("/") + 1, nodePath.length());
			boolean ignore = false;
			for (String s : IGNORED_PROPERTIES) {
				if (propName.contains(s)) {
					ignore = true;
					break;
				}
			}

			for (String s : IGNORED_PROPERTY_PATTERNS) {
				if (propName.contains(s)) {
					ignore = true;
					break;
				}
			}
			if (!ignore) {
				int indexOfLastSlash = nodePath.lastIndexOf("/");
				int indexOfContent = nodePath.lastIndexOf("/jcr:content");
				// String absolutePath = AUDIT_NODE_PATH + nodePath.substring(0,
				// indexOfLastSlash) + "/" + auditNodeName;
				String absolutePath = AUDIT_NODE_PATH + nodePath.substring(0, indexOfContent) + "/" + auditNodeName;
				String propertyChanged = nodePath.substring(indexOfLastSlash + 1, nodePath.length());

				Node auditNode = JcrUtil.createPath(absolutePath, false, "sling:Folder", "cq:AuditEvent", session,
						false);
				auditNode.setProperty("path", nodePath);
				auditNode.setProperty("cq:path", nodePath);
				auditNode.setProperty("type", action);
				auditNode.setProperty("cq:type", action);
				auditNode.setProperty("cq:category", AUDIT_CATEGORY);
				auditNode.setProperty("cq:time", calendar);
				auditNode.setProperty("cq:userid", userId);
				auditNode.setProperty("userId", userId);
				auditNode.setProperty("propertyChanged", propertyChanged);
				String value = session.getProperty(nodePath).getString();
				auditNode.setProperty("propertyChangedValue", value);
				auditNode.setProperty("componentName", componentName);
				auditNode.setProperty("componentType", "contentFragment");
			}

			session.save();
		} catch (RepositoryException e) {
			LOGGER.error("Failed to create audit node : {}", e.getMessage());
		}

	}

}