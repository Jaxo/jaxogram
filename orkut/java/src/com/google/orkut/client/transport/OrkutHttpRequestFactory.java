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

/**
 * A {@link HttpRequestFactory} that gives out {@link OrkutHttpRequest}s.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class OrkutHttpRequestFactory implements HttpRequestFactory {
  public HttpRequest getHttpRequest(byte[] body) {
    return new OrkutHttpRequest(body);
  }
}
