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

import com.google.orkut.client.api.Constants.Gender;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

/**
 * Represents the profile information for a person.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class OrkutPerson {

  protected JSONObject json;

  OrkutPerson(JSONObject json) {
    this.json = json;
  }

  private JSONObject getNameField() {
    return json.optJSONObject(Fields.NAME);
  }

  /** Returns given name or null if the field was not present in the response */
  public String getGivenName() {
    JSONObject nameObj = getNameField();
    return nameObj != null ? nameObj.optString(Fields.NAME_GIVEN_NAME, null) : null;
  }

  /** Returns family name or null if the field was not present in the response */
  public String getFamilyName() {
    JSONObject nameObj = getNameField();
    return nameObj != null ? nameObj.optString(Fields.NAME_FAMILY_NAME, null) : null;
  }

  /** Returns display name or null if the field was not present in the response */
  public String getDisplayName() {
    String givenName = getGivenName();
    String familyName = getFamilyName();
    if (givenName == null && familyName == null) {
      return null;
    }

    if (givenName == null) {
      return familyName;
    }

    if (familyName == null) {
      return givenName;
    }

    return givenName + " " + familyName;
  }

  /** Returns thumbnail-url or null if the field was not present in the response */
  public String getThumbnailUrl() {
    return json.optString(Fields.THUMBNAIL_URL, null);
  }

  /** Returns the status, or null if not present */
  public String getStatus() {
    return json.optString(Fields.STATUS, null);
  }

  /** Returns the number of email entries present */
  public int getEmailCount() {
    JSONArray emailsArray = json.optJSONArray(Fields.EMAILS);
    if (emailsArray == null) {
      return 0;
    }
    return emailsArray.length();
  }

  /** Returns the email address at the given index */
  public String getEmail(int index) {
    JSONArray emailsArray = json.optJSONArray(Fields.EMAILS);
    if (emailsArray == null) {
      return null;
    }
    return emailsArray.optJSONObject(index).optString(Fields.VALUE);
  }

  /** Returns the gender of the person (from {@link Gender#MALE} or {@link Gender#FEMALE})
   * or <code>null</code>, if not available.
   */
  public String getGender() {
    String value = json.optString(Fields.GENDER);
    if (Gender.FEMALE.equals(value)) {
      return Gender.FEMALE;
    }

    if (Gender.MALE.equals(value)) {
      return Gender.MALE;
    }

    return null;
  }

  /** Returns the count of phone numbers available */
  public int getPhoneNumberCount() {
    JSONArray phoneNumbers = json.optJSONArray(Fields.PHONE_NUMBERS);
    if (phoneNumbers == null) {
      return 0;
    }
    return phoneNumbers.length();
  }

  /** Returns the phone number at the given index */
  public String getPhoneNumber(int index) {
    JSONArray phoneNumbers = json.optJSONArray(Fields.PHONE_NUMBERS);
    if (phoneNumbers == null) {
      return null;
    }
    return phoneNumbers.optJSONObject(index).optString(Fields.VALUE);
  }

  /** Returns the profile url of the person */
  public String getProfileUrl() {
    return json.optString(Fields.PROFILE_URL);
  }

  /** Returns the birthday of the person as #seconds since epoch */
  public long getBirthday() {
    return json.optLong(Fields.BIRTHDAY);
  }

  /** Returns the address of the person, if any */
  public Address getAddress() {
    JSONObject addrObject = json.optJSONObject(Fields.CURRENT_LOCATION);
    return addrObject == null ? null : new Address(addrObject);
  }

  /**
   * Returns the underlying json-object from which this resource has been constructed.
   * Clients using this function should use it to get data-items that does not have a canned 'get'
   * method. Clients are not supposed to modify the underlying object.
   */
  JSONObject getJSONbject() {
    return json;
  }

  public String getId() {
    return Util.JSONUtil.getRequiredStringField("id", getJSONbject());
  }
}
