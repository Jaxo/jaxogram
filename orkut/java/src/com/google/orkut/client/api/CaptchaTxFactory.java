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

/**
 * Creates a transaction to submit a solution to captcha challenge.
 * 
 * <p>Below is an example of someone attempting to post scrap with a url in the
 * body. At first a captcha error is thrown, but once the request is resubmitted
 * with solution to captcha the request goes through.
 * </p>
 * 
 * <pre>{@code
 * // attempt to write a scrap with url in the body.
 * ScrapTxFactory scrapTxFactory = new ScrapTxFactory();
 * WriteScrapTx writeScrapTx = scrapTxFactory.writeScrap(personId, bodyWithUrl);
 * execute(writeScrapTx);
 *   
 * if (writeScrapTx.hasError()) {
 *   OrkutError error = writeScrapTx.getError();
 *   if (error.isCaptchaError()) {
 *     // solve captcha by showing the url to the user.
 *     String answer = solveCaptha(error.captchaUrl());
 *     String token = error.captchaToken();
 *      
 *     Transaction captchaTx = new CaptchaTxFactory().answerCaptcha(token, answer);
 *     // add captchaTx along with the transactions that received captcha error
 *     // and execute them together.
 *     writeScrapTx = scrapTxFactory.writeScrap(personId, bodyWithUrl);
 *     execute(writeScrapTx, captchaTx);  // run them in the same batch
 *       
 *     if (!writeScrapTx.hasError()) {
 *       // Scrap with URL got posted!
 *     }
 *   }
 * }
 * }
 * 
 * </pre>
 * @author Sachin Shenoy
 */
public class CaptchaTxFactory {

  /**
   * Creates a {@link Transaction} that submits solution to captcha.
   * 
   * @param captchaToken token received in captcha error from method
   *     {@link OrkutError#captchaToken()}
   * @param captchaAnswer answer to the captcha challenge
   * @return a transaction to submit captcha solution. 
   */
  public Transaction answerCaptcha(String captchaToken, String captchaAnswer) {
    Transaction transaction = new Transaction(RequestIds.CAPTCHA_ANSWER,
        MethodNames.CAPTCHA_ANSWER);
    transaction.request.setUserId(Constants.USERID_ME)
        .setGroupId(Group.SELF)
        .addParameter(Params.CAPTCHA_TOKEN, captchaToken)
        .addParameter(Params.CAPTCHA_ANSWER, captchaAnswer);
    return transaction;
  }
}
