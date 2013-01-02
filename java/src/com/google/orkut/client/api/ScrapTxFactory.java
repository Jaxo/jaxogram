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

import com.google.orkut.client.api.InternalConstants.Values;

/**
 * Factory for creating {@link Transaction}s related to Scraps.
 *
 * @author Sachin Shenoy
 */
public class ScrapTxFactory {

  /**
   * Get a {@link GetScrapsTx} to fetch scraps of the logged in user.
   */
  public GetScrapsTx getSelfScraps() {
    return new GetScrapsTx();
  }

  /**
   * Get a {@link GetScrapsTx} to fetch scraps of a person.
   */
  public GetScrapsTx getScrapsOf(String personId) {
    return new GetScrapsTx(personId);
  }

  /**
   * Get a {@link GetScrapsTx} to fetch the next set of scraps,
   * give a previous successfully executed {@link GetScrapsTx}.
   */
  public GetScrapsTx getNext(GetScrapsTx prev) {
    return prev.getNext();
  }

  /**
   * Get a {@link GetScrapsTx} to fetch the prev set of scraps,
   * give a previously successfully executed {@link GetScrapsTx}.
   */
  public GetScrapsTx getPrev(GetScrapsTx last) {
    return last.getPrev();
  }

  /**
   * Get a {@link Transaction} to delete a scrap.
   * @param scrapEntry the scrap to delete
   */
  public Transaction deleteScrap(ScrapEntry scrapEntry) {
    Transaction transaction = new Transaction(RequestIds.SCRAPS_DELETE, MethodNames.MESSAGES_DELETE);
    transaction.request.setUserId(Constants.USERID_ME)
                       .setGroupId(Group.SELF)
                       .addParameter(Params.MESSAGE_TYPE, Values.PUBLIC_MESSAGE)
                       .addParameter(Params.MSG_ID, scrapEntry.getId());
    return transaction;
  }

  /**
   * Get a {@link WriteScrapTx} to write a scrap in a person's scrap book.
   */
  public WriteScrapTx writeScrap(String personId, String body) {
    return new WriteScrapTx(personId, body);
  }

  /**
   * Get a {@link WriteScrapTx} to reply to a scrap message.
   */
  public WriteScrapTx replyToScrap(ScrapEntry scrapEntry, String body) {
    return new WriteScrapTx(scrapEntry.getFromUserId(), body, scrapEntry.getId());
  }
  /**
   * Get a {@link WriteScrapTx} to reply to a scrap message.
   */
  public WriteScrapTx replyToScrap(String userId, String scrapId, String body) {
    return new WriteScrapTx(userId, body, scrapId);
  }
}
