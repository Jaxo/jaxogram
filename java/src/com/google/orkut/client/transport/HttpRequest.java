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

import com.google.orkut.client.api.BatchTransaction;

import java.util.Collection;

/**
 * Represents an HTTP request to be sent on the wire. This class is created
 * using {@link BatchTransaction#build()}. Clients are supposed to form an
 * HTTP request using the values returned by this class, add authentication to
 * it and send to orkut servers.
 *
 * Look at {@link com.google.orkut.client.sample.Transport} for an example in samples/.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public interface HttpRequest {

  public static class Parameter {
    String key;
    String value;

    Parameter(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }

  public static class Header {
    String name;
    String value;

    Header(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * Returns the body of the request
   */
  byte[] getRequestBody();

  /**
   * Returns Collection of {@link Parameter} to be sent with the HTTP request.
   */
  Collection getParameters();

  /**
   * Returns Colleciton of {@link Header} to be sent with the HTTP request.
   */
  Collection getHeaders();

  /**
   * Returns the method of the request.
   */
  String getMethod();

  /**
   * Returns the url where the request is to be sent.
   */
  String getRequestBaseUrl();

  /**
   * Adds a parameter to the list of parameters of the http request.
   */
  HttpRequest addParam(String key, String value);

  /**
   * Adds a header to the list of http-headers.
   */
  HttpRequest addHeader(String name, String value);

  /**
   * Sets the HTTP request method.
   */
  HttpRequest setMethod(String method);

  /**
   * Sets the Request URL base.
   */
  HttpRequest setRequestBaseUrl(String requestBaseUrl);
}
