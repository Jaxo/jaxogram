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

package com.google.orkut.client.api;

import org.json.me.JSONObject;

public class RequiredFieldNotFoundException extends RuntimeException {
  private final String fieldName;
  private final JSONObject obj;
  RequiredFieldNotFoundException(String fieldName, JSONObject obj) {
    super("Required field (" + fieldName + ") not present in " + obj.toString());
    this.fieldName = fieldName;
    this.obj = obj;
  }

  public String getField() {
    return fieldName;
  }

  public JSONObject getJSONObject() {
    return obj;
  }
}
