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
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.apache.jackrabbit.commons.flat.TreeTraverser;
import org.apache.sling.api.resource.LoginException;
import com.bounteous.aem.audit.services.ComponentAuditPurge;
import com.bounteous.aem.audit.AuditConstants;

@Component(immediate = true, service = ComponentAuditPurge.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Component Audit Purge Service" })
public class ComponentAuditPurgeImpl implements ComponentAuditPurge {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAuditPurgeImpl.class);    
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    protected void activate() {

    }

    @Override
    public void purgeEvents(int minimumAge) {
    	final String AUDITPATH = AuditConstants.AUDIT_ROOT_PATH + "/" + AuditConstants.AUDIT_LOG_CATEGORY;
        Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, AuditConstants.SUB_SERVICE_ID);
        
        try  {
        	ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authenticationInfo);
        	Session purgeSession = resolver.adaptTo(Session.class);
        	Resource auditRootResource = resolver.getResource(AUDITPATH);
        	Node auditResourceNode = auditRootResource.adaptTo(Node.class);
        	Iterator<Node> auditNodes = new TreeTraverser(auditResourceNode).iterator();
        	while (auditNodes.hasNext()) {
        		Node auditNode = auditNodes.next();
        		if(auditNode.getProperty("jcr:primaryType").getString().equals("cq:AuditEvent")) {
        			Date auditDate = auditNode.getProperty("cq:time").getDate().getTime();
        			Calendar currentCal = Calendar.getInstance();
        	        currentCal.add(Calendar.DATE, -minimumAge);
                	Date purgeDate = currentCal.getTime();             	
                	if(auditDate.compareTo(purgeDate) < 0) {
                		auditNode.remove();
                     } 
        		}
        	}
        	
        	purgeSession.save();

        } catch (LoginException e) {
            LOGGER.error(e.getMessage());

        } catch (ValueFormatException e) {
        	 LOGGER.error(e.getMessage());
		} catch (PathNotFoundException e) {
			 LOGGER.error(e.getMessage());
		} catch (RepositoryException e) {
			 LOGGER.error(e.getMessage());
		}

    }

    
}
