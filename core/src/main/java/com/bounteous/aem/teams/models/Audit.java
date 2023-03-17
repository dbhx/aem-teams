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
import java.util.List;


public class Audit {

    private AuditUser auditUser;
    private Date date;
    private List<AuditHistory> history;
    private String lastModified;
    private List<AuditUser> pageEditors;

    public AuditUser getAuditUser() {
        return auditUser;
    }

    public Date getDate() {
        return date;
    }

    public List<AuditHistory> getHistory() {
        return history;
    }

    public String getLastModified() {
        return lastModified;
    }

    public List<AuditUser> getPageEditors() {
        return pageEditors;
    }


    public void setAuditUser(AuditUser auditUser) {
        this.auditUser = auditUser;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHistory(List<AuditHistory> history) {
        this.history = history;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setPageEditors(List<AuditUser> pageEditors) {
        this.pageEditors = pageEditors;
    }

}
