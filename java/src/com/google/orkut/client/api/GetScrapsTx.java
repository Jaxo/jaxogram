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

import java.util.Vector;

/**
 * A {@link Transaction} to fetch scraps for a user on orkut.
 *
 * @author Sachin Shenoy
 */
public class GetScrapsTx extends Transaction {
  private static final String MESSAGE_FORMAT = "messageFormat";
  private static final int DEFAULT_NUM_SCRAPS = 10;
  private Vector scraps = new Vector();

  public static class MessageFormat {
    public static String NO_HTML = "noHtml";
    public static String FULL_HTML = "fullHtml";
    public static String PLAIN_TEXT = "plainText";

    private static String getMessageFormatType(String format) {
      if (NO_HTML.equals(format)) {
        return NO_HTML;
      }
      if (PLAIN_TEXT.equals(format)) {
        return PLAIN_TEXT;
      }
      if (FULL_HTML.equals(format)) {
        return FULL_HTML;
      }
      return FULL_HTML;
    }
  }

  GetScrapsTx(String personId) {
    super(RequestIds.SCRAPS_GET, MethodNames.MESSAGES_GET);
    request.setUserId(personId)
           .setGroupId(Group.FRIENDS)
           .addParameter(Fields.PAGE_TYPE, Params.PageType.FIRST)
           .addParameter(Fields.MESSAGE_TYPE, InternalConstants.Values.PUBLIC_MESSAGE);
    setCount(DEFAULT_NUM_SCRAPS);
  }

  GetScrapsTx() {
    this(Constants.USERID_ME);
  }

  public GetScrapsTx setMessageFormat(String format) {
    request.addParameter(MESSAGE_FORMAT, MessageFormat.getMessageFormatType(format));
    return this;
  }


  public GetScrapsTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  GetScrapsTx getNext() {
    GetScrapsTx scrapTx = new GetScrapsTx(request.getUserId());
    scrapTx.request.addParameter(Fields.PAGE_TYPE, Params.PageType.NEXT)
                   .addParameter(Params.COUNT, request.getCount())
                   .addParameter(Params.LAST_KEY, getLastMessageKey());
    return scrapTx;
  }

  GetScrapsTx getPrev() {
    GetScrapsTx scrapTx = new GetScrapsTx(request.getUserId());
    scrapTx.request.addParameter(Fields.PAGE_TYPE, Params.PageType.PREV)
                   .addParameter(Params.COUNT, request.getCount())
                   .addParameter(Params.LAST_KEY, getFirstMessageKey());
    return scrapTx;
  }

  protected void setResponseData(JSONObject data) {
    scraps = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
          Object convert(JSONObject json) {
            return new ScrapEntry(json);
          }
        });
  }

  public int getScrapCount() {
    return scraps.size();
  }

  public ScrapEntry getScrap(int index) {
    return (ScrapEntry) scraps.get(index);
  }

  private String getFirstMessageKey() {
    if (scraps.isEmpty()) {
      return "";
    }
    ScrapEntry scrapEntry = (ScrapEntry) scraps.firstElement();
    return scrapEntry.getId();
  }

  private String getLastMessageKey() {
    if (scraps.isEmpty()) {
      return "";
    }
    ScrapEntry scrapEntry = (ScrapEntry) scraps.lastElement();
    return scrapEntry.getId();
  }
}
