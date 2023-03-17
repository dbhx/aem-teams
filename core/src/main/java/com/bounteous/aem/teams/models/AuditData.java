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

package com.bounteous.aem.teams.models;

import java.util.List;

/**
 * Provides provides the ability to store and retrieve audit information and
 * group chat link information.
 *
 */

public class AuditData {

    private List<Audit> audits;
    private boolean isTeamsOn;
    private boolean isTeamsGroupValid;
    private String pagePath;
    private boolean showGroupChatLink;
    private String teamsTenantId;

    public List<Audit> getAudits() {
        return audits;
    }

    public boolean getShowGroupChatLink() {
        return showGroupChatLink;
    }

    public boolean getIsTeamsOn() {
        return isTeamsOn;
    }
    
    public boolean getIsTeamsGroupValid() {
        return isTeamsGroupValid;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getTeamsTenantId() {
        return teamsTenantId;
    }


    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }

    public void setIsTeamsOn(boolean isTeamsOn) {
        this.isTeamsOn = isTeamsOn;
    }
    
    public void setIsTeamsGroupValid(boolean isTeamsGroupValid) {
        this.isTeamsGroupValid = isTeamsGroupValid;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public void setShowGroupChatLink(boolean showGroupChatLink) {
        this.showGroupChatLink = showGroupChatLink;
    }

    public void setTeamsTenantId(String teamsTenantId) {
        this.teamsTenantId = teamsTenantId;
    }

}
