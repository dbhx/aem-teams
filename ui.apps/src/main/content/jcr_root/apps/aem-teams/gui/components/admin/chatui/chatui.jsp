<%--
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
 --%><%
%><%@page session="false" contentType="text/html; charset=utf-8"%><%
%><%@page import="java.util.List,
                  java.util.ArrayList,
                  java.util.Iterator,
                  java.util.ListIterator,
                  com.bounteous.aem.teams.models.AuditHistory,
                  com.bounteous.aem.teams.models.Audit,
                  com.bounteous.aem.teams.models.AuditUser,
                  com.bounteous.aem.teams.models.AuditData,
                  com.bounteous.aem.teams.services.ComponentHistoryService,
                  com.bounteous.aem.teams.AemTeamsConstants,
                  org.apache.sling.api.resource.ResourceResolver"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><cq:defineObjects /><%
    ResourceResolver resolver = slingRequest.getResourceResolver();
    Session session = resolver.adaptTo(Session.class);
    String requestUri = request.getRequestURI();
    String pagePath = request.getParameter("path");
    ComponentHistoryService chs = sling.getService(ComponentHistoryService.class);
    AuditData auditData = chs.getHistoryCollection(pagePath, session.getUserID());
    List<Audit> audits;
    if (auditData != null) {
        audits = auditData.getAudits();
    } else {
        audits = new ArrayList<>();
    }
    %>
<div id="main-chat-panel" class="coral-Form-fieldwrapper aem-lock-chat-panel cmp-chat-ui">
<%  if (audits.size() == 0) { %>
        <div class="cmp-chat-ui__no-history"><%=AemTeamsConstants.NO_HISTORY_FOUND_MESSAGE%></div>
<% } else {
        Iterator<Audit> itr = audits.iterator();
		while (itr.hasNext()) {
			Audit audit = itr.next();
			AuditUser auditUser = audit.getAuditUser();
			List<AuditHistory> histories = audit.getHistory();
			ListIterator<AuditHistory> historyIt = histories.listIterator();
			%>
        <div class="cmp-chat-ui__accordion" data-teamsid="<%=auditUser.getTeamsId()%>">
            <span class="cmp-chat-ui__group-add">
                <label class="cmp-chat-ui__group-add-checkbox">
                    <input class="cmp-chat-ui__group-add-input" type="checkbox" name="selectedUser" checked>
                    <span class="cmp-chat-ui__group-add-indicator"></span>
                </label>
            </span>
            <div class="cmp-chat-ui__user-info">
                <div class="cmp-chat-ui__username">
                    <%=auditUser.getFirstName()%> <%=auditUser.getLastName()%>
                </div>
                <div class="cmp-chat-ui__user-last-modified">
                    Last Update: <%=audit.getLastModified()%>
                </div>
            </div>
        </div>
        <div class="cmp-chat-ui__edits-panel">
         <% while (historyIt.hasNext()) {
                int index = historyIt.nextIndex();
                AuditHistory history = historyIt.next();
                boolean isLast = false;
                if (index == (histories.size() - 1)) {
                    isLast = true;
                } %>
            <div class="cmp-chat-ui__edit-item-heading cmp-chat-ui__edit-item-heading--<%=history.getAction()%>">
                <span class="cmp-chat-ui__edit-item-action">
                    <%=history.getAction()%>
                </span>
                <span class="cmp-chat-ui__edit-item-modified">
                    <%=history.getModified()%>
                </span>
            </div>
                   <%  if (history.getPersists()) { %>
                        <div class="cmp-chat-ui__component cmp-chat-ui__component--selectable" data-contentitem="<%=history.getItemContentPath()%>">
                            <% if (!history.getCreated() && !history.getAction().equals("deleted")) { %>
                            <span class="cmp-chat-ui__component-location">
                                <span class="cmp-chat-ui__component-action"><%=history.getAction()%></span>
                                <span class="cmp-chat-ui__component-info"><%=history.getFieldName() %></span> in
                            <% } else { %>
                            <span class="cmp-chat-ui__component-location">
                                <span class="cmp-chat-ui__component-action"><%=history.getAction()%></span>
                            <% } %>
                                <span class="cmp-chat-ui__component-info"><%=history.getComponentName()%></span>
                            </span>
                            <span class="cmp-chat-ui__component-content-path">Content path: <span class="cmp-chat-ui__component-info"><%=history.getItem()%></span></span>
                        </div>
                        <%if(history.getAction().equals("updated")){%>
                            <div class="cmp-chat-ui__component"><button class="modalButton" is="coral-button" data-id="<%=history.getUid() %>">VIEW CONTENT</button></div>
							<div id="<%=history.getUid()%>" class="modal">
  								<div class="modal-content">
    								<p><%=history.getFieldValue() %></p>
  								</div>

							</div>
                            <%}%>
                    <% } else { %>
                        <div class="cmp-chat-ui__component">
                            <% if (!history.getCreated() && !history.getAction().equals("deleted")) { %>
                            <span class="cmp-chat-ui__component-location">
                                <span class="cmp-chat-ui__component-action"><%=history.getAction()%></span>
                                <span class="cmp-chat-ui__component-info"><%=history.getFieldName() %></span> in
                            <% } else { %>
                            <span class="cmp-chat-ui__component-location">
                                <span class="cmp-chat-ui__component-action"><%=history.getAction()%></span>
                            <% } %>
                                <span class="cmp-chat-ui__component-info"><%=history.getComponentName()%></span>
                            </span>
                            <span class="cmp-chat-ui__component-content-path">Content path: <span class="cmp-chat-ui__component-info"><%=history.getItem()%></span></span>
                        </div>
                    <% }%>
                    <div class="cmp-chat-ui__chat-buttons">
                     <% if (isLast) {
                            if (auditData.getIsTeamsOn()) {
                                if (auditUser.getIsTeamsIdValid()) { %>
                                    <button data-page="<%=auditData.getPagePath() %>"
                                        data-teamsid="<%=auditUser.getTeamsId()%>"
                                        data-tenantid="<%=auditData.getTeamsTenantId()%>"
                                        class="cmp-chat-ui__teams-chat-btn cmp-chat-ui__teams-chat-btn--individual"
                                        href="#">
                                        TEAMS CHAT
                                    </button>
                             <% } else { %>
                                    <button class="cmp-chat-ui__teams-chat-btn cmp-chat-ui__teams-chat-btn--individual" disabled>TEAMS CHAT</button>
                             <% }
                            }
                        } %>
                    </div>
          <% } %>
        </div>
	 <% } %>
        <div class="cmp-chat-ui__chat-buttons">
     <%
		if (auditData.getIsTeamsOn()) {
            if (auditData.getShowGroupChatLink() && auditData.getIsTeamsGroupValid()) { %>
                <button class="cmp-chat-ui__teams-chat-btn cmp-chat-ui__teams-chat-btn--group" id="teams-group-chat" href="#" data-page="<%=auditData.getPagePath() %>" data-tenantid="<%=auditData.getTeamsTenantId()%>">TEAMS GROUP CHAT</button>
         <% } else { %>
                <button class="cmp-chat-ui__teams-chat-btn cmp-chat-ui__teams-chat-btn--group" disabled>TEAMS GROUP CHAT</button>
         <% }
       } %>
        </div>
<%  } %>
</div>


        
         

