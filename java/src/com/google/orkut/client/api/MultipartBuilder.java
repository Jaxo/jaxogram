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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Builds a multi-part HTTP request.
 *
 * @author Sachin Shenoy
 */
class MultipartBuilder {

  private static final String UTF_8 = "UTF-8";
  private static final String MULTIPART_FORM_DATA = "multipart/form-data; boundary=";
  private static final byte[] FIELD_PARAM = getUtf8("Content-Disposition: form-data; name=");
  private static final byte[] FILE_PARAM = getUtf8("; filename=");
  private static final byte[] CONTENT_TYPE = getUtf8("Content-Type: ");
  private static final byte[] QUOTE = getUtf8("\"");
  private static final byte[] CRLF = getUtf8("\r\n");
  private static final byte[] DASHDASH = getUtf8("--");

  private final ByteArrayOutputStream buffer;
  private final String boundary;
  private final byte[] boundaryAsBytes;

  MultipartBuilder() {
    buffer = new ByteArrayOutputStream();
    boundary = "n1z2y3x4w5v6u7t";
    boundaryAsBytes = boundary.getBytes();
  }

  private static byte[] getUtf8(String value) {
    try {
      return value.getBytes(UTF_8);
    } catch (UnsupportedEncodingException e) {
      // cannot encode this as utf8? - this should not happen!
      throw new RuntimeException("Cannot convert string to UTF8", e);
    }
  }

  MultipartBuilder addField(String name, String value) throws IOException {
    buffer.write(DASHDASH);
    buffer.write(boundaryAsBytes);
    buffer.write(CRLF);
    buffer.write(FIELD_PARAM);
    buffer.write(QUOTE);
    buffer.write(getUtf8(name));
    buffer.write(QUOTE);
    buffer.write(CRLF);
    buffer.write(CRLF);
    buffer.write(getUtf8(value));
    buffer.write(CRLF);
    return this;
  }

  MultipartBuilder addFile(String name, String filename,
      String contentType, byte[] value) throws IOException {
    buffer.write(DASHDASH);
    buffer.write(boundaryAsBytes);
    buffer.write(CRLF);
    buffer.write(FIELD_PARAM);
    buffer.write(QUOTE);
    buffer.write(getUtf8(name));
    buffer.write(QUOTE);
    buffer.write(FILE_PARAM);
    buffer.write(QUOTE);
    buffer.write(getUtf8(filename));
    buffer.write(QUOTE);
    buffer.write(CRLF);
    buffer.write(CONTENT_TYPE);
    buffer.write(getUtf8(contentType));
    buffer.write(CRLF);
    buffer.write(CRLF);
    buffer.write(value);
    buffer.write(CRLF);
    return this;
  }

  byte[] build() throws IOException {
    buffer.write(DASHDASH);
    buffer.write(boundaryAsBytes);
    buffer.write(DASHDASH);
    buffer.write(CRLF);
    buffer.write(CRLF);

    return buffer.toByteArray();
  }

  String getContentType() {
    return MULTIPART_FORM_DATA + boundary;
  }
}
