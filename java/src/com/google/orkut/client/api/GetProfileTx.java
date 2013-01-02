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
 * Transaction for fetching profile of a user. Use {@link ProfileTxFactory} to
 * create instances of this class.
 *
 * @author Sachin Shenoy
 */
public class GetProfileTx extends Transaction {
  private OrkutPerson orkutPerson;

  GetProfileTx() {
    this(Constants.USERID_ME);
  }

  GetProfileTx(String userId) {
    super(RequestIds.PEOPLE_GET, MethodNames.PEOPLE_GET);
    request.setUserId(userId).setGroupId(Group.SELF);
  }

  public GetProfileTx alsoGetName() {
    request.addFieldIfNotPresent(Fields.NAME);
    return this;
  }

  public GetProfileTx alsoGetThumbnailUrl() {
    request.addFieldIfNotPresent(Fields.THUMBNAIL_URL);
    return this;
  }

  public GetProfileTx alsoGetProfileUrl() {
    request.addFieldIfNotPresent(Fields.PROFILE_URL);
    return this;
  }

  public GetProfileTx alsoGetStatus() {
    request.addFieldIfNotPresent(Fields.STATUS);
    return this;
  }

  public GetProfileTx alsoGetEmails() {
    request.addFieldIfNotPresent(Fields.EMAILS);
    return this;
  }

  public GetProfileTx alsoGetGender() {
    request.addFieldIfNotPresent(Fields.GENDER);
    return this;
  }

  public GetProfileTx alsoGetPhoneNumbers() {
    request.addFieldIfNotPresent(Fields.PHONE_NUMBERS);
    return this;
  }

  public GetProfileTx alsoGetBirthday() {
    request.addFieldIfNotPresent(Fields.BIRTHDAY);
    return this;
  }

  public GetProfileTx alsoGetAddress() {
    request.addFieldIfNotPresent(Fields.CURRENT_LOCATION);
    return this;
  }

  protected void setResponseData(JSONObject response) {
    orkutPerson = new OrkutPerson(response);
  }

  public OrkutPerson getProfile() {
    return orkutPerson;
  }
}
