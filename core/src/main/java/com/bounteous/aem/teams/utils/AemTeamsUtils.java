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

package com.bounteous.aem.teams.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Objects;

import static com.bounteous.aem.teams.AemTeamsConstants.PN_EMAIL;
import static com.bounteous.aem.teams.AemTeamsConstants.PROFILE;

public class AemTeamsUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AemTeamsUtils.class);

    public static String getUserEmail(String userId, UserManager userManager, ResourceResolver resolver) {
        String email = StringUtils.EMPTY;

        try {
            User user = (User) userManager.getAuthorizable(userId);
            Resource profile = resolver.getResource(Objects.requireNonNull(user).getPath() + PROFILE);
            if (profile != null) {
                ValueMap profileValues = profile.getValueMap();
                if (profileValues.containsKey(PN_EMAIL)) {
                    email = profileValues.get(PN_EMAIL, StringUtils.EMPTY);
                }
            }
        } catch (RepositoryException e) {
            LOG.error("Exception while getting user email associated with user id : {}", userId);
            LOG.error(e.getMessage());
        }
        return email;
    }

}
