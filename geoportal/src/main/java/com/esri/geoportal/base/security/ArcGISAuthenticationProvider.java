/* See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Esri Inc. licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri.geoportal.base.security;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.net.ssl.SSLContext;

import com.esri.geoportal.base.util.JsonUtil;
import com.esri.geoportal.base.util.Val;
import com.esri.geoportal.context.GeoportalContext;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Authentication for ArcGIS OAuth2.
 */
@Component
public class ArcGISAuthenticationProvider implements AuthenticationProvider {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISAuthenticationProvider.class);

  /** Instance variables. */
  private boolean allUsersCanPublish = false;
  private String appId;
  private String authorizeUrl; 
  private String createAccountUrl;
  private int expirationMinutes = 120;
  private String geoportalAdministratorsGroupId;
  private String geoportalPublishersGroupId;  
  private String rolePrefix;
  private boolean showMyProfileLink = false;

  private String clientCertificatePath = null;
  private String clientCertificateKey = null;
  private String trustStorePath =  null;
  private String trustStoreKey =  null;

  /** True if all authenticated users shoudl have a Publisher role. */
  public boolean getAllUsersCanPublish() {
    return allUsersCanPublish;
  }
  /** True if all authenticated users should have a Publisher role. */
  public void setAllUsersCanPublish(boolean allUsersCanPublish) {
    this.allUsersCanPublish = allUsersCanPublish;
  }

  /** The ArcGIS application item id that will be used for OAuthe authentication. */
  public String getAppId() {
    return appId;
  }
  /** The ArcGIS application item id that will be used for OAuth authentication. */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  /** The ArcGIS OAuth authorize URL. */
  public String getAuthorizeUrl() {
    return authorizeUrl;
  }
  /** The ArcGIS OAuth authorize URL. */
  public void setAuthorizeUrl(String authorizeUrl) {
    this.authorizeUrl = authorizeUrl;
  }

  /** The create account URL. */
  public String getCreateAccountUrl() {
    return createAccountUrl;
  }
  /** The create account URL. */
  public void setCreateAccountUrl(String createAccountUrl) {
    this.createAccountUrl = createAccountUrl;
  }
  
  /** Token expiration minutes. */
  public int getExpirationMinutes() {
    return expirationMinutes;
  }
  /** Token expiration minutes. */
  public void setExpirationMinutes(int expirationMinutes) {
    this.expirationMinutes = expirationMinutes;
  }

  /** The id of the ArcGIS group containing Geoportal administrators (optional). */
  public String getGeoportalAdministratorsGroupId() {
    return geoportalAdministratorsGroupId;
  }
  /** The id of the ArcGIS group containing Geoportal administrators (optional). */
  public void setGeoportalAdministratorsGroupId(String geoportalAdministratorsGroupId) {
    this.geoportalAdministratorsGroupId = geoportalAdministratorsGroupId;
  }

  /** The id of the ArcGIS group containing Geoportal publishers (optional). */
  public String getGeoportalPublishersGroupId() {
    return geoportalPublishersGroupId;
  }
  /** The id of the ArcGIS group containing Geoportal publishers (optional). */
  public void setGeoportalPublishersGroupId(String geoportalPublishersGroupId) {
    this.geoportalPublishersGroupId = geoportalPublishersGroupId;
  }
  
  /** The Spring role prefix. */
  public String getRolePrefix() {
    return rolePrefix;
  }
  /** The Spring role prefix. */
  public void setRolePrefix(String rolePrefix) {
    this.rolePrefix = rolePrefix;
  }
  
  /** If true, show My Profile link. */
  public boolean getShowMyProfileLink() {
    return showMyProfileLink;
  }
  /** If true, show My Profile link. */
  public void setShowMyProfileLink(boolean showMyProfileLink) {
    this.showMyProfileLink = showMyProfileLink;
  }

    /**
   * @return the clientCertificatePath
   */
  public String getClientCertificatePath() {
    return clientCertificatePath;
  }
  /**
   * @param clientCertificatePath the clientCertificatePath to set
   */
  public void setClientCertificatePath(String clientCertificatePath) {
    this.clientCertificatePath = clientCertificatePath;
  }
  /**
   * @return the clientCertificateKey
   */
  public String getClientCertificateKey() {
    return clientCertificateKey;
  }
  /**
   * @param clientCertificateKey the clientCertificateKey to set
   */
  public void setClientCertificateKey(String clientCertificateKey) {
    this.clientCertificateKey = clientCertificateKey;
  }

  /**
   * @return the trustStorePath
   */
  public String getTrustStorePath() {
    return trustStorePath;
  }
  /**
   * @param trustStorePath the trustStorePath to set
   */
  public void setTrustStorePath(String trustStorePath) {
    this.trustStorePath = trustStorePath;
  }
  /**
   * @return the trustStoreKey
   */
  public String getTrustStoreKey() {
    return trustStoreKey;
  }
  /**
   * @param trustStoreKey the trustStoreKey to set
   */
  public void setTrustStoreKey(String trustStoreKey) {
    this.trustStoreKey = trustStoreKey;
  }

  /** Methods =============================================================== */
  
  /**
   * Get the roles for a user.
   * @param username the username 
   * @param token the ArcGIS token
   * @param referer the HTTP referer
   * @return the roles
   * @throws AuthenticationException
   */
  private List<GrantedAuthority> executeGetRoles(String username, String token, String referer) 
      throws AuthenticationException {
    List<GrantedAuthority> roles = new ArrayList<>();
    List<String> groupKeys = new ArrayList<>();
    String adminGroupId = this.getGeoportalAdministratorsGroupId();
    String pubGroupId = this.getGeoportalPublishersGroupId();
    boolean allUsersCanPublish = this.getAllUsersCanPublish();
    boolean isInAdminGroup = false;
    boolean isInPubGroup = false;
    boolean hasOrgAdminRole = false;
    boolean hasOrgPubRole = false;
    boolean hasOrgUserRole = false; 

    StringBuilder url = new StringBuilder(this.getRestUrl());
    if (url.charAt(url.length() - 1) == '/') {
      url.deleteCharAt(url.length() - 1);
    }

    try {
      if (getClientCertificateKey() != null && getClientCertificatePath() != null) {
        url.append("/community/users/");
        url.append(URLEncoder.encode(username,"UTF-8"));
        url.append("?f=json");
      } else {
        url.append("/community/self");
        url.append("?f=json&token=");
        url.append(URLEncoder.encode(token, "UTF-8"));
      }
    } catch (UnsupportedEncodingException e) {}

    RestTemplate rest = getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    if (referer != null) {
      headers.add("Referer",referer);
    };
    HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
    ResponseEntity<String> responseEntity = rest.exchange(url.toString(),HttpMethod.GET,requestEntity,String.class);
    String response = responseEntity.getBody();

    //System.err.println(response);;
    //if (response != null) LOGGER.trace(response);
    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new AuthenticationServiceException("Error communicating with the authentication service.");
    }
    JsonObject jso = (JsonObject)JsonUtil.toJsonStructure(response);

    // Check for error from portal
    if (jso.containsKey("error")) {
      JsonObject errObj = jso.getJsonObject("error");
      throw new AuthenticationServiceException("Error communicating with authenticaton service: " + errObj.getString("message"));
    }

    if (jso.containsKey("role") && !jso.isNull("role")) {
      String role = jso.getString("role");
      if (role.equals("org_admin") || role.equals("account_admin")) hasOrgAdminRole = true;
      if (role.equals("org_publisher") || role.equals("account_publisher")) hasOrgPubRole = true;
      if (role.equals("org_user") || role.equals("account_user")) hasOrgUserRole = true;
    }

    if (jso.containsKey("groups") && !jso.isNull("groups")) {
      JsonArray jsoGroups = jso.getJsonArray("groups");
      for (int i=0;i<jsoGroups.size();i++) {
        JsonObject jsoGroup = jsoGroups.getJsonObject(i);
        String groupId = jsoGroup.getString("id");
        String groupName = jsoGroup.getString("title");
        String groupKey = groupId+"_..._"+groupName;
        groupKeys.add(groupKey);
        if ((adminGroupId != null) && (adminGroupId.length() > 0) && adminGroupId.equals(groupId)) {
          isInAdminGroup = true;
        }
        if ((pubGroupId != null) && (pubGroupId.length() > 0) && pubGroupId.equals(groupId)) {
          isInPubGroup = true;
        }
      }
    }

    boolean isAdmin = false;
    boolean isPublisher = false;
    if ((adminGroupId != null) && (adminGroupId.length() > 0)) {
      if (isInAdminGroup) isAdmin = true;
    } else {
      if (hasOrgAdminRole) isAdmin = true;
    }
    if (allUsersCanPublish) {
      if (hasOrgAdminRole || hasOrgPubRole || hasOrgUserRole) {
        isPublisher = true;
      } else {
        // This is a Public account (Facebook, ...)
        isPublisher = true;
      }
    }
    if ((pubGroupId != null) && (pubGroupId.length() > 0)) {
      if (isInPubGroup) isPublisher = true;
    } else {
      if (hasOrgPubRole) isPublisher = true;
    }

    String pfx = Val.chkStr(this.getRolePrefix(),"").trim();
    if (isAdmin) {
      roles.add(new SimpleGrantedAuthority(pfx+"ADMIN"));
      if (!isPublisher) roles.add(new SimpleGrantedAuthority(pfx+"PUBLISHER"));
    }
    if (isPublisher) {
      roles.add(new SimpleGrantedAuthority(pfx+"PUBLISHER"));
    }
    
    if (jso.containsKey("username") && !jso.isNull("username")) {
      if (!username.equals(jso.getString("username"))) {
        throw new BadCredentialsException("Credential mismatch.");
      }
      roles.add(new SimpleGrantedAuthority(pfx+"USER"));
    } else {
      throw new BadCredentialsException("Credential mis-match.");
    }
    
    GeoportalContext gc = GeoportalContext.getInstance();
    if (gc.getSupportsGroupBasedAccess()) {
      for (String groupKey: groupKeys) {
        roles.add(new SimpleGrantedAuthority(groupKey));
      }
    }
    return roles;
  }

  /**
   * Get an ArcGIS token.
   * @param username the username
   * @param password the password
   * @param referer the HTTP referer
   * @return the token
   * @throws AuthenticationException
   */
  private String executeGetToken(String username, String password, String referer) 
      throws AuthenticationException {
    if (username == null || username.length() == 0 || password == null || password.length() == 0) {
      throw new BadCredentialsException("Invalid credentials.");
    }
    
    //System.err.println("password="+password);;
    if (password.startsWith("__rtkn__:")) {
      String token = password.substring(9);
      //System.err.println("token="+token);
      return token;
    }
  
    String token = null;
    String restBaseUrl = this.getRestUrl();
    String url = restBaseUrl+"/generateToken";
    //System.err.println("generateTokenUrl="+url);
    StringBuilder content = new StringBuilder();

    try {
      content.append("f=json");
      content.append("&username=").append(URLEncoder.encode(username,"UTF-8"));
      content.append("&password=").append(URLEncoder.encode(password,"UTF-8"));
      content.append("&expiration=").append(URLEncoder.encode(""+getExpirationMinutes(),"UTF-8"));
      content.append("&referer=").append(URLEncoder.encode(referer,"UTF-8"));
      content.append("&client=").append(URLEncoder.encode("referer","UTF-8"));
    } catch (UnsupportedEncodingException e) {}

    RestTemplate rest = getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type","application/x-www-form-urlencoded");
    HttpEntity<String> requestEntity = new HttpEntity<String>(content.toString(),headers);
    ResponseEntity<String> responseEntity = rest.exchange(url,HttpMethod.POST,requestEntity,String.class);
    String response = responseEntity.getBody();
    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new AuthenticationServiceException("Error communicating with the authentication service.");
    }
    JsonObject jso = (JsonObject)JsonUtil.toJsonStructure(response);
    if (jso.containsKey("token")) {
      token = jso.getString("token");
    }
    if (token == null || token.length() == 0) {
      throw new BadCredentialsException("Invalid credentials.");
    }
    return token;
  }
  
  /**
   * Get the ArcGIS portal URL.
   * @return the url
   */
  public String getPortalUrl() {
    return getRestUrl();
  }

  /**
   * Get the ArcGIS rest URL.
   * @return the url
   */
  public String getRestUrl() {
    String authorizeUrl = this.getAuthorizeUrl();
    if (authorizeUrl.indexOf("/sharing/oauth2/") > 0) {
      return authorizeUrl.substring(0,authorizeUrl.indexOf("/sharing/oauth2/"))+"/sharing/rest";
    }
    return authorizeUrl.substring(0,authorizeUrl.indexOf("/sharing/rest/oauth2/"))+"/sharing/rest";
  }

  /**
   * Get the local host name (to be used as a referer).
   * @return the referer
   */
  private String getThisReferer() {
    try {
      return InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException ex) {
      return "";
    }
  }

  /** AuthenticationProvider API ============================================ */

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    LOGGER.debug("ArcGISAuthenticationProvider::authenticate");
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    String referer = getThisReferer();
    String token = executeGetToken(username,password,referer);
    List<GrantedAuthority> roles = executeGetRoles(username,token,referer);
    return new UsernamePasswordAuthenticationToken(username,password,roles);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

  private RestTemplate getRestTemplate() {
    LOGGER.trace("ArcGISAuthenticationProvider:getRestTemplate");

    RestTemplate template = null;

    // Determine if we need to set up a client certificate
    if (this.getClientCertificatePath() != null && this.getClientCertificateKey() != null) {
      // Set up the SSL connection as necessary
      SSLContext sslContext = null;
      try {
        char[] password = this.getClientCertificateKey().toCharArray();
        char[] trustPwd = this.getTrustStoreKey().toCharArray();

        // Load in the keystore
        File key = ResourceUtils.getFile(this.getClientCertificatePath()); 
        File trustStore = ResourceUtils.getFile(this.getTrustStorePath());

        sslContext = SSLContextBuilder.create().loadKeyMaterial(key, password, password).loadTrustMaterial(trustStore, trustPwd).build();
      } catch (Exception e) {
        LOGGER.error("Exception while trying to create SSL context", e);
      }

      HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
      HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

      template = new RestTemplate(factory);
    } else {
      template = new RestTemplate();
    }

    return template;
  }


}
