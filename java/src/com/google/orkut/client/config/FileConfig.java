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

package com.google.orkut.client.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * {@link Config} while reads configuration from {@link #CONFIG_FILE}.
 * You may choose to create an implementation with hard-coded values,
 * instead of reading from this properties file.
 *
 * @author Shishir Birmiwal
 */
public class FileConfig implements Config {
  public final String CONFIG_FILE = "sample/oauth.properties";
  private final String SERVER_URL = "serverUrl";
  private final Properties props;

  public FileConfig() throws IOException {
    FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE);
    props = new Properties();
    props.load(fileInputStream);
    fileInputStream.close();
  }

  public String getRequestBaseUrl() {
    return props.getProperty(SERVER_URL);
  }
}
