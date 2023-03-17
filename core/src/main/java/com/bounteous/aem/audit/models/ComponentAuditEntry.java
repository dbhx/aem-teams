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

package com.bounteous.aem.audit.models;

import java.util.Date;

public class ComponentAuditEntry {

    private String category;
    private Date date;
    private String type;
    private String userId;
    private String path;
    private String propertyAdded;
    private String propertyAddedValue;
    private String propertyChanged;
    private String propertyChangedValue;
    private String componentName;
    private boolean hasBefore;
    private String before;
    private String componentType;
    private String uid;

    public String getUid() {
        return uid;
    }
    
    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getPath() {
        return path;
    }
    
    public String getComponentName() {
        return componentName;
    }
    
    public String getPropertyAdded() {
        return propertyAdded;
    }
    
    public String getPropertyAddedValue() {
        return propertyAddedValue;
    }
    
    public String getPropertyChanged() {
        return propertyChanged;
    }
    
    public String getPropertyChangedValue() {
        return propertyChangedValue;
    }
    
    public boolean getHasBefore() {
        return hasBefore;
    }
    
    public String getBefore() {
        return before;
    }

    public String getComponentType() {
        return componentType;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
    
    public void setPropertyAdded(String propertyAdded) {
        this.propertyAdded = propertyAdded;
    }
    
    public void setPropertyAddedValue(String propertyAddedValue) {
        this.propertyAddedValue = propertyAddedValue;
    }
    
    public void setPropertyChanged(String propertyChanged) {
        this.propertyChanged = propertyChanged;
    }
    
    public void setPropertyChangedValue(String propertyChangedValue) {
        this.propertyChangedValue = propertyChangedValue;
    }
    
    public void setHasBefore(boolean hasBefore) {
        this.hasBefore = hasBefore;
    }
    
    public void setBefore(String before) {
        this.before = before;
    }
    
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

}
