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

import java.util.Date;

public class AuditHistory {

    private String action;
    private String componentName;
    private String componentType;
    private boolean created;
    private Date date;
    private String fieldName;
    private String fieldValue;
    private boolean isActivate;
    private String item;
    private String itemContentPath;
    private String modified;
    private boolean persists;
    private String userId;
    private boolean hasDiff;
    private String uid;


    public String getUid() {
        return uid;
    }
    
    public String getAction() {
        return action;
    }

    public String getComponentName() {
        return componentName;
    }
    
    public String getComponentType() {
        return componentType;
    }

    public boolean getCreated() {
        return created;
    }

    public Date getDate() {
        return date;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }

    public boolean getIsActivate() {
        return isActivate;
    }

    public String getItem() {
        return item;
    }

    public String getItemContentPath() {
        return itemContentPath;
    }

    public String getModified() {
        return modified;
    }

    public boolean getPersists() {
        return persists;
    }
    public String getUserId() {
        return userId;
    }

    public boolean getHasDiff() {
        return hasDiff;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
    
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setIsActivate(boolean isActivate) {
        this.isActivate = isActivate;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setItemContentPath(String itemContentPath) {
        this.itemContentPath = itemContentPath;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setPersists(boolean persists) {
        this.persists = persists;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setHasDiff(boolean hasDiff) {
        this.hasDiff = hasDiff;
    }

}
