/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/20/2013
*/
package com.jaxo.googapp.jaxogram;

/*-- class JsonIterator --+
*//**
* A very simple JSon parser, fast and powerful enough for what we want.
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class JsonIterator {
   String m_data;
   int m_start;

   /*------------------------------------------------------------JsonIterator-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   JsonIterator(String data) {
      m_data = data;
      m_start = -1;
   }

   /*--------------------------------------------------------------------next-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   String next(String name) {
      name = "\"" + name + "\"";
      int start = m_data.indexOf(name, m_start+1);
      if (start >= 0) {
         m_start = m_data.indexOf('"', start+name.length()+1) + 1;
         return m_data.substring(m_start, m_start=m_data.indexOf('"', m_start));
      }else {
         return null;
      }
   }

   /*---------------------------------------------------------------------get-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static public String get(String data, String name) {
      name = "\"" + name + "\"";
      int start = data.indexOf(name);
      if (start >= 0) {
         start = data.indexOf('"', start+name.length()+1) + 1;
         return data.substring(start, data.indexOf('"', start));
      }else {
         return null;
      }
   }
}
/*===========================================================================*/

