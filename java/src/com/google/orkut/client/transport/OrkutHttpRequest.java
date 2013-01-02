/* EXPERIMENTAL (really) */
/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.orkut.client.transport;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An model implementation of {@link HttpRequest}.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class OrkutHttpRequest implements HttpRequest {

  private final byte[] body;
  private final ArrayList params;
  private final ArrayList headers;
  private String method;
  private String requestBaseUrl;

  public OrkutHttpRequest(byte[] body) {
    this.body = body;
    this.params = new ArrayList();
    this.headers = new ArrayList();
  }

  /* (non-Javadoc)
   * @see com.google.orkut.client.transport.HttpRequest#getRequestBody()
   */
  public byte[] getRequestBody() {
    return body;
  }

  /* (non-Javadoc)
   * @see com.google.orkut.client.transport.HttpRequest#getParameters()
   */
  public Collection getParameters() {
    return params;
  }

  /* (non-Javadoc)
   * @see com.google.orkut.client.transport.HttpRequest#getHeaders()
   */
  public Collection getHeaders() {
    return headers;
  }

  public HttpRequest addParam(String key, String value) {
    params.add(new Parameter(key, value));
    return this;
  }

  public HttpRequest addHeader(String name, String value) {
    headers.add(new Header(name, value));
    return this;
  }

  public String getMethod() {
    return method;
  }

  /**
   * NOTE:
   * <p>This value is for ORKUT SANDBOX. DO NOT GO TO PRODUCTION
   * with this value. For production, this should be
   *   http://www.orkut.com/social/rpc
   */
  public String getRequestBaseUrl() {
    return requestBaseUrl;
  }

  public HttpRequest setMethod(String method) {
    this.method = method;
    return this;
  }

  public HttpRequest setRequestBaseUrl(String requestBaseUrl) {
    this.requestBaseUrl = requestBaseUrl;
    return this;
  }
}
