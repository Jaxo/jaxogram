package com.google.orkut.client.api;

/** Exception in OrkutAdapter. This class represents an exception
 *  generated by {@link OrkutAdapter}, signaling that an error
 *  in the protocol or library usage has occurred. Notice that this
 *  is different from server-side errors, which are not signaled by
 *  means of exceptions but instead as a flag on each transaction.
 *  For more information on this, please see the {@link Transaction}
 *  and {@link BatchTransaction} classes. */
@SuppressWarnings("serial")
public class OrkutAdapterException extends RuntimeException {
   public OrkutAdapterException(String msg, Throwable cause) {
      super(msg,cause);
   }
}
