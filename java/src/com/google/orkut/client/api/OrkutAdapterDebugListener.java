package com.google.orkut.client.api;

/** A listener for OrkutAdapter debug messages. A listener of this kind
 *  can be installed in an {@link OrkutAdapter} in order receive its
 *  debug messages. */
public interface OrkutAdapterDebugListener {
   /** Should print/log the debug message. This method is supposed to
    *  print or log the given debug message. */
   public void printOrkutAdapterMessage(String msg);
}

