/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.expertsystem.spheres.oauth2.controller;


import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.glassfish.jersey.server.Uri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.expertsystem.spheres.oauth2.model.OAuthParams;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;

/**
 * Handles requests for the application welcome page.
 */

public class AuthzController {

    private Logger logger = LoggerFactory.getLogger(AuthzController.class);


    public Response authorize(OAuthParams oauthParams)
        throws OAuthSystemException, IOException {

        logger.debug("start processing /authorize request");
         
        OAuthClientRequest request = OAuthClientRequest
            .authorizationLocation(oauthParams.getAuthzEndpoint())
            .setClientId(oauthParams.getClientId())
            .setRedirectURI(oauthParams.getRedirectUri())
            .setResponseType(ResponseType.CODE.toString())
            .setScope(oauthParams.getScope())
            .setState(oauthParams.getState())
            .buildQueryMessage();

            logger.debug("loc uri {}",request.getLocationUri());
            return Response.seeOther(URI.create(request.getLocationUri())).					 
					 cookie(new NewCookie("clientId", oauthParams.getClientId())).            						 
					 cookie(new NewCookie("clientSecret", oauthParams.getClientSecret())).
					 cookie(new NewCookie("authzEndpoint", oauthParams.getAuthzEndpoint())).
					 cookie(new NewCookie("tokenEndpoint", oauthParams.getTokenEndpoint())).
					 cookie(new NewCookie("redirectUri", oauthParams.getRedirectUri())).
					 cookie(new NewCookie("scope", oauthParams.getScope())).
					 cookie(new NewCookie("state", oauthParams.getState())).
					 cookie(new NewCookie("app", oauthParams.getApplication())).
					 build();
            
//            return new ModelAndView(new RedirectView(request.getLocationUri()));
//        } catch (ApplicationException e) {
//            logger.error("failed to validate OAuth authorization request parameters", e);
//            oauthParams.setErrorMessage(e.getMessage());
//            return new ModelAndView("get_authz");
//        }
    }

}
