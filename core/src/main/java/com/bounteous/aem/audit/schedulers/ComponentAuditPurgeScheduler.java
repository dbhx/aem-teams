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

package com.bounteous.aem.audit.schedulers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bounteous.aem.audit.services.ComponentAuditPurge;

@Designate(ocd=ComponentAuditPurgeScheduler.Config.class)
@Component(service=Runnable.class)
public class ComponentAuditPurgeScheduler implements Runnable {
	
	@Reference
	private ComponentAuditPurge componentAuditPurge;

    @ObjectClassDefinition(name="Component Audit Purge Scheduler",
                           description = "Component Audit Purge Scheduler")
    public static @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "0 0/5 * * * ?";

        @AttributeDefinition(name = "Concurrent task",
                             description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(name = "Minimum Age",
                             description = "Minimum age (in days) of audit log entries to be purged, default 60")
        int minimumAge() default 60;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private int minimumAge;  

    @Activate
    protected void activate(final Config config) {
        minimumAge = config.minimumAge();
        LOGGER.info("Component AuditPurge Scheduler activated with, minimumAge='{}'", minimumAge);
        
    }
    
    @Override
    public void run() {
        LOGGER.info("ComponentAuditPurge is now running, minimumAge='{}'", minimumAge);
        componentAuditPurge.purgeEvents(minimumAge);
    }

}
