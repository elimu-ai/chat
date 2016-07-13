// IConnectionCallback.aidl
package chat.literacyapp.org.chat.bluetooth;


/**
* The oneway keyword modifies the behavior of remote calls. When used, a remote call does not block; it simply sends the transaction data and immediately returns.
*
*/
oneway interface IConnectionCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      void incomingConnection(String device);
       void maxConnectionsReached();
       void messageReceived(String device, String message);
       void connectionLost(String device);

}
