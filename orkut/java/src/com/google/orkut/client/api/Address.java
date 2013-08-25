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

/**
 * The address of a user.
 *
 * @author Shishir Birmiwal
 */
public class Address {
  private final JSONObject json;

  public Address() {
    this(new JSONObject());
  }

  Address(JSONObject json) {
    this.json = json;
  }

  public String getCountryCode() {
    return json.optString(Fields.COUNTRY);
  }

  public String getRegion() {
    return json.optString(Fields.REGION);
  }

  public String getLocality() {
    return json.optString(Fields.LOCALITY);
  }

  public String getPostalCode() {
    return json.optString(Fields.POSTAL_CODE);
  }

  public Address setCountryCode(String code) {
    Util.putJsonValue(json, Fields.COUNTRY, code);
    return this;
  }

  public Address setRegion(String region) {
    Util.putJsonValue(json, Fields.REGION, region);
    return this;
  }

  public Address setLocality(String locality) {
    Util.putJsonValue(json, Fields.LOCALITY, locality);
    return this;
  }

  public Address setPostalCode(String postalCode) {
    Util.putJsonValue(json, Fields.POSTAL_CODE, postalCode);
    return this;
  }

  JSONObject getJson() {
    return json;
  }

  @Override
  public String toString() { 
     try {
        return json.toString(3);
     }
     catch (org.json.me.JSONException ex) { return "???"; }
  }
}
