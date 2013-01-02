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

import com.google.orkut.client.config.Config;
import com.google.orkut.client.config.FileConfig;
import com.google.orkut.client.transport.HttpRequest;
import com.google.orkut.client.transport.HttpRequestFactory;
import com.google.orkut.client.transport.OrkutHttpRequestFactory;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Builds a batch request from {@link Transaction}s.
 *
 * <pre>
 * {@code
 * BatchTransaction batch = new BatchTransaction();
 * httpRequest = batch.add(transaction1)
 *      .add(transaction2)
 *      .add(transaction3)
 *      .build();
 *
 * batch.setResponse(response);
 * }</pre>
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class BatchTransaction {
  private static final String REQUEST_PARAM = "request";
  private static final String UTF_8 = "UTF-8";
  private static final String APPLICATION_JSON = "application/json";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String REQUEST_METHOD = "POST";

  /**
   * Map of request-id to transaction. Used to de-mux responses to appropriate
   * transaction
   */
  private final HashMap transactions = new HashMap();
  private final ArrayList transactionsV = new ArrayList();

  /**
   * Array of all JSON request in this batch.
   */
  private final JSONArray batch = new JSONArray();

  private final HttpRequestFactory requestFactory;
  private final Config config;

  /**
   * Create a {@link BatchTransaction} using the given requestFactory and config.
   */
  public BatchTransaction(HttpRequestFactory requestFactory, Config config) {
    this.requestFactory = requestFactory;
    this.config = config;
  }

  /**
   * Convenience constructor that creates a {@link BatchTransaction} using the
   * given requestFactory and {@link FileConfig}.
   */
  public BatchTransaction(HttpRequestFactory requestFactory) throws IOException {
    this(requestFactory, new FileConfig());
  }

  /**
   * Convenience constructor that creates a {@link BatchTransaction}.
   * To use a custom {@link HttpRequestFactory} or a custom {@link Config},
   * use {@link BatchTransaction#BatchTransaction(HttpRequestFactory)} or
   * {@link BatchTransaction#BatchTransaction(HttpRequestFactory, Config)}.
   */
  public BatchTransaction() throws IOException {
    this(new OrkutHttpRequestFactory());
  }

  /**
   * Adds the given request to the batch.
   *
   * @param transaction transaction to be added to the batch.
   * @return this to facilitate chaining
   */
  public BatchTransaction add(Transaction transaction) {
    if (transaction == null) {
      throw new NullPointerException("transaction cannot be null ");
    }
    transactions.put(transaction.getId(), transaction);
    batch.put(transaction.getRequestAsJson());
    transactionsV.add(transaction);
    return this;
  }

  public int getTransactionCount() { return transactionsV.size(); }
  public Transaction getTransaction(int i) {
     return (Transaction) transactionsV.get(i);
  }

  public boolean hasErrors() {
     int i;
     for (i = 0; i < getTransactionCount(); i++)
        if (getTransaction(i).hasError()) return true;
     return false;
  }

  public String getErrorsAsString() {
     int i;
     StringBuilder sb = new StringBuilder();
     for (i = 0; i < getTransactionCount(); i++)
        if (getTransaction(i).hasError()) sb.append(getTransaction(i).getError().toString() + "\n");
     return sb.toString();
  }

  public HttpRequest build() throws IOException {
    HttpRequest request;
    if (hasUpload()) {
      MultipartBuilder builder = new MultipartBuilder();
      addBody(builder);
      request = requestFactory.getHttpRequest(builder.build());
      request.addParam(REQUEST_PARAM, batch.toString());
      request.addHeader(CONTENT_TYPE, builder.getContentType());
    } else {
      request = requestFactory.getHttpRequest(batch.toString().getBytes(UTF_8));
      request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
    }
    request.addHeader(InternalConstants.ORKUT_CLIENT_LIB_HEADER, InternalConstants.VERSION_STRING);
    request.setMethod("POST");
    request.setRequestBaseUrl(config.getRequestBaseUrl());
    return request;
  }

  private boolean hasUpload() {
    Iterator it = transactions.values().iterator();
    while (it.hasNext()) {
       Transaction transaction = (Transaction) it.next();
      if (transaction instanceof UploadPhotoTx) {
        return true;
      }
    }
    return false;
  }

  private void addBody(MultipartBuilder builder) throws IOException {
    Iterator it = transactions.values().iterator();
    while (it.hasNext()) {
       Transaction transaction = (Transaction) it.next();
      if (transaction instanceof UploadPhotoTx) {
        UploadPhotoTx photoTx = (UploadPhotoTx) transaction;
        photoTx.addBody(builder);
      }
    }
  }
  /**
   * Set the response received from server for the batch request.
   *
   * @param batchResponseString
   */
  public void setResponse(String batchResponseString) {
    JSONArray batchResponse;
    try {
      batchResponse = new JSONArray(batchResponseString);
      for (int i = 0; i < batchResponse.length(); ++i) {
        JSONObject response = batchResponse.getJSONObject(i);
        String id = response.optString(Fields.ID);
        Transaction transaction = (Transaction) transactions.get(id);
        if (transaction != null) {
          transaction.setResponse(response);
        }
      }
    } catch (JSONException e) {
      throw new RuntimeException("Unexpected exception while setting response", e);
    }
  }
}
