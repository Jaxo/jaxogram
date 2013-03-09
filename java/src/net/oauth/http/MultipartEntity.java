/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/9/2013
*/
package net.oauth.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;

/*-- class MultipartEntity --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
class MultipartEntity {
   private static final byte[] FIELD_PARAM = (
      "Content-Disposition: form-data; name=".getBytes()
   );
   private static final byte[] FILENAME = "; filename=".getBytes();
   private static final byte[] CONTENT_TYPE = "Content-Type: ".getBytes();
   private static final byte[] QUOTE = {(byte)'\"'};
   private static final byte[] CRLF = {(byte)'\r',(byte)'\n'};
   private static final byte[] CRLFCRLF = {(byte)'\r',(byte)'\n',(byte)'\r',(byte)'\n'};
   private static final byte[] DASHDASH = {(byte)'-',(byte)'-'};
   private final static char[] MULTIPART_CHARS = (
      "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
   ).toCharArray();

   private ByteArrayOutputStream m_tempBuf;
   private String m_contentType;
   private byte[] m_boundary;
   private Vector<InputStream> m_streams;

   /*---------------------------------------------------------MultipartEntity-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   MultipartEntity() {
      String boundary = generateBoundary();
      m_contentType = "multipart/form-data; boundary=" + boundary;
      m_boundary = ("--" + boundary).getBytes();
      m_tempBuf = new ByteArrayOutputStream();
      m_streams = new Vector<InputStream>();
   }

   /*----------------------------------------------------------------addField-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void addField(String name, String value, String encoding)
   throws IOException, UnsupportedEncodingException
   {
      writeProlog(encoding, name, null, null);
      m_tempBuf.write(value.getBytes());
      m_tempBuf.write(CRLF);
      m_streams.add(new ByteArrayInputStream(m_tempBuf.toByteArray()));
   }

   /*-----------------------------------------------------------------addFile-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void addFile(
      String name,
      String filename,
      String contentType,  // "image/jpeg"
      byte[] data
   ) throws IOException, UnsupportedEncodingException {
      writeProlog("UTF-8", name, filename, contentType);
      m_streams.add(new ByteArrayInputStream(m_tempBuf.toByteArray()));
      m_streams.add(new ByteArrayInputStream(data));
      m_streams.add(new ByteArrayInputStream(CRLF));
   }

   /*---------------------------------------------------------------addStream-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void addStream(
      String name,
      String filename,
      String contentType,  // "image/jpeg"
      InputStream stream
   ) throws IOException, UnsupportedEncodingException {
      writeProlog("UTF-8", name, filename, contentType);
      m_streams.add(new ByteArrayInputStream(m_tempBuf.toByteArray()));
      m_streams.add(stream);
      m_streams.add(new ByteArrayInputStream(CRLF));
   }

   /*----------------------------------------------------------getContentType-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String getContentType() {
      return m_contentType;
   }

   /*-----------------------------------------------------------------getBody-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public InputStream getBody() throws IOException {
      m_tempBuf.reset();
      m_tempBuf.write(m_boundary);
      m_tempBuf.write(DASHDASH);
      m_tempBuf.write(CRLFCRLF);
      m_streams.add(new ByteArrayInputStream(m_tempBuf.toByteArray()));
      return new SequenceInputStream(m_streams.elements());
   }

   /*-------------------------------------------------------------writeProlog-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private void writeProlog(
      String encoding,
      String name,
      String filename,
      String contentType
   )
   throws IOException, UnsupportedEncodingException
   {
      m_tempBuf.reset();
      m_tempBuf.write(m_boundary);
      m_tempBuf.write(CRLF);
      m_tempBuf.write(FIELD_PARAM);
      m_tempBuf.write(QUOTE);
      m_tempBuf.write(name.getBytes(encoding));
      m_tempBuf.write(QUOTE);
      if (filename != null) {
         m_tempBuf.write(FILENAME);
         m_tempBuf.write(QUOTE);
         m_tempBuf.write(filename.getBytes(encoding));
         m_tempBuf.write(QUOTE);
      }
      if (contentType != null) {
         m_tempBuf.write(CRLF);
         m_tempBuf.write(CONTENT_TYPE);
         m_tempBuf.write(contentType.getBytes(encoding));
      }
      m_tempBuf.write(CRLFCRLF);
   }

   /*--------------------------------------------------------generateBoundary-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private String generateBoundary() {
      StringBuilder sb = new StringBuilder();
      Random rand = new Random();
      for (int i=0, iMax=rand.nextInt(11)+30; i < iMax; ++i) {
         sb.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
      }
      return sb.toString();
   }
}
/*===========================================================================*/
