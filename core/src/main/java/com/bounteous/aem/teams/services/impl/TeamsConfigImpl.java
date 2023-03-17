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

import com.bounteous.aem.teams.services.TeamsConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = TeamsConfig.class)
@Designate(ocd = TeamsConfigImpl.Cfg.class)
public class TeamsConfigImpl implements TeamsConfig {

    private boolean teamsEnabled;
    private String teamsTenantId;
    private boolean chatTabEnabled;

    @Activate
    @Modified
    protected void activate(TeamsConfigImpl.Cfg config) {
        this.teamsEnabled = config.getTeamsEnabled();
        this.teamsTenantId = config.getTeamsTenantId();
        this.chatTabEnabled = config.getChatTabEnabled();
    }

    @Override
    public boolean getTeamsEnabled() {
        return teamsEnabled;
    }

    @Override
    public String getTeamsTenantId() {
        return teamsTenantId;
    }

    @Override
    public boolean getChatTabEnabled() {
        return chatTabEnabled;
    }

    @ObjectClassDefinition(name = "AEM Teams - General Configuration")
    public @interface Cfg {

        @AttributeDefinition(name = "Enable Teams", description = "Allow users to use the Teams deeplinking feature")
        boolean getTeamsEnabled() default true;

        @AttributeDefinition(name = "Teams Tenant Id", description = "Tenant ID used to create deep links for Teams")
        String getTeamsTenantId() default "";

        @AttributeDefinition(name = "Enable Chat Tab", description = "Add the chat tab to the content rail")
        boolean getChatTabEnabled() default true;

    }
}
