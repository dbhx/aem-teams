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

public class AuditUser {

    private String email;
    private String firstName;
    private boolean isTeamsIdValid;
    private String lastName;
    private String teamsId;
    private String userId;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public boolean getIsTeamsIdValid() {
        return isTeamsIdValid;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTeamsId() {
        return teamsId;
    }

    public String getUserId() {
        return userId;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setIsTeamsIdValid(boolean isTeamsIdValid) {
        this.isTeamsIdValid = isTeamsIdValid;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTeamsId(String teamsId) {
        this.teamsId = teamsId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
