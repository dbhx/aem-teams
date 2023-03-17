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

package com.bounteous.aem.audit.impl;

import com.bounteous.aem.audit.ComponentAuditConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component(
    immediate = true,
    service = ComponentAuditConfig.class
)
@Designate(ocd = ComponentAuditConfigImpl.Config.class)
public class ComponentAuditConfigImpl implements ComponentAuditConfig {

    private List<String> contentPathList;
    private List<String> componentPathList;
    private List<String> ignoredPropertiesList;
    private List<String> ignoredPropertyPatternsList;

    @Override
    public List<String> getComponentFolderList() {
        return Collections.unmodifiableList(contentPathList);
    }
    
    @Override
    public List<String> getExcludedComponentsList() {
        return Collections.unmodifiableList(componentPathList);
    }
    
    @Override
    public List<String> getIgnoredPropertiesList() {
        return Collections.unmodifiableList(ignoredPropertiesList);
    }
    
    @Override
    public List<String> getIgnoredPropertyPatternsList() {
        return Collections.unmodifiableList(ignoredPropertyPatternsList);
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        this.contentPathList = Arrays.asList(config.contentPathList());
        this.componentPathList = Arrays.asList(config.componentPathList());
        this.ignoredPropertiesList = Arrays.asList(config.ignoredPropertiesList());
        this.ignoredPropertyPatternsList = Arrays.asList(config.ignoredPropertyPatternsList());
    }


    @ObjectClassDefinition(name = "AEM - Component Audit Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Audited Content Paths", description = "The content paths that use component auditing.")
        String[] contentPathList() default {};
        @AttributeDefinition(name = "Excluded Components", description = "The components that should be excluded from auditing.")
        String[] componentPathList() default {};
        @AttributeDefinition(name = "Ignored Properties", description = "The properties that should be excluded from auditing.")
        String[] ignoredPropertiesList() default {};
        @AttributeDefinition(name = "Ignored Property Patterns", description = "The property patterns that should be excluded from auditing.")
        String[] ignoredPropertyPatternsList() default {};
    }
}
