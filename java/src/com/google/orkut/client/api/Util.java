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

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Contains utility functions required by the orkut client library
 *
 * @author Hari S
 */
public class Util {
  private Util() {
  }


  public static String getHttpVersionHeaderName() {
    return InternalConstants.ORKUT_CLIENT_LIB_HEADER;
  }

  public static String getHttpVersionHeaderValue() {
    return InternalConstants.VERSION_STRING;
  }

  public static boolean isEmpty(String str) {
    return null == str || "".equals(str);
  }

  public static boolean isEmptyOrWhiteSpace(String str) {
    return null == str || 0 == str.trim().length();
  }

  static String getRuntimeErrorMessage(String methodName) {
    return methodName + " : Unexpected exception ";
  }

  static void putJsonValue(JSONObject json, String key, Object value) {
    try {
      json.put(key, value);
    } catch (JSONException e) {
      throw new RuntimeException("Null key while writing into json", e);
    }
  }
  
  static String getFormattedTimestamp(long timeMillis) {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Constants.DateFormatter.UTC));
    cal.setTimeInMillis(timeMillis);
    StringBuilder date = new StringBuilder();
    date.append(cal.get(Calendar.YEAR));
    date.append(Constants.DateFormatter.DATE_SEPARATOR);
    date.append(padSingleDigitNum(cal.get(Calendar.MONTH) + 1));
    date.append(Constants.DateFormatter.DATE_SEPARATOR);
    date.append(padSingleDigitNum(cal.get(Calendar.DATE)));
    date.append(Constants.DateFormatter.DATE_DELIM);
    date.append(padSingleDigitNum(cal.get(Calendar.HOUR)));
    date.append(Constants.DateFormatter.TIME_SEPARATOR);
    date.append(padSingleDigitNum(cal.get(Calendar.MINUTE)));
    date.append(Constants.DateFormatter.TIME_SEPARATOR);
    date.append(padSingleDigitNum(cal.get(Calendar.SECOND)));
    date.append(Constants.DateFormatter.TIME_DELIM);
    return date.toString();
  }
  
  static private String padSingleDigitNum(int num) {
    return (num < 10) ? ("0" + num) : ("" + num);
  }


  static Vector forEachItemInList(JSONObject data, String key, Converter processor) {
    Vector items = new Vector();
    try {
      JSONArray itemList = data.getJSONArray(key);
      if (itemList == null) {
        return items;
      }
      int numItems = itemList.length();
      for (int i = 0; i < numItems; i++) {
        JSONObject json = itemList.getJSONObject(i);
        try {
          items.add(processor.convert(json));
        } catch (CreationException e) {
          // ignore and skip conversion of this item
        } catch (RuntimeException e) {
          // we skip any runtime exception too. This is to catch any exception
          // that occurs while the object is being initialized.
          // TODO(Sachin Shenoy): Don't silently eat up exception, provide
          // mechanism to log these. 
        }
      }
    } catch (JSONException jse) {
      throw new RuntimeException("Unexpected json exception.", jse);
    }
    return items;
  }
   
   private static byte[] resizeVec(byte[] b, int newSize) {
      byte[] c = new byte[newSize];
      System.arraycopy(b,0,c,0,b.length > newSize ? newSize : b.length);
      return c;
   }

   /** Reads an input stream til EOF and returns all bytes read.
    *  The stream will NOT be closed.
    *
    *  @param is The input stream to read.
    *  @return An array of bytes that represents all bytes read
    *  from the stream until the end-of-file is reached.
    */
   public static byte[] readAllFrom(java.io.InputStream is) 
                        throws java.io.IOException {
      byte[] buf = new byte[8192];
      int i = 0;
      int n = 0, r;
      final int SIZE = 4096;

      while (true) {
         // invariant: buf[0..n-1] has the valid data
         // and buffer capacity is >= n

         // make sure buffer has capacity at least n + SIZE
         while (buf.length <= n + SIZE + 1)
            buf = resizeVec(buf, 2 * buf.length);

         // read into buf
         r = is.read(buf, n, SIZE);
         if (r <= 0) break; // end of stream

         n += r;
      }

      // resize the byte array to its correct size
      return resizeVec(buf, n);
   }

   /** Load a file and returns its content as a byte array.
    *
    *  @param fileName the name of the file to load.
    *  @return The full contents of the file as a byte array. */
   public static byte[] loadFile(String fileName) throws java.io.IOException {
      java.io.FileInputStream fin = new java.io.FileInputStream(fileName);
      byte[] b = readAllFrom(fin);
      fin.close();
      return b;
   }

  static class JSONUtil {

    static String getRequiredStringField(String fieldName, JSONObject dataObject) {
      try {
        return dataObject.getString(fieldName);
      } catch (JSONException jse) {
        throw new RequiredFieldNotFoundException(fieldName, dataObject);
      }
    }

    static int getRequiredIntField(String fieldName, JSONObject dataObject) {
      try {
        return dataObject.getInt(fieldName);
      } catch (JSONException jse) {
        throw new RequiredFieldNotFoundException(fieldName, dataObject);
      }
    }

    static JSONObject getRequiredJSONObjectField(String fieldName, JSONObject dataObject) {
      try {
        return dataObject.getJSONObject(fieldName);
      } catch (JSONException jse) {
        throw new RequiredFieldNotFoundException(fieldName, dataObject);
      }
    }
  }
}
