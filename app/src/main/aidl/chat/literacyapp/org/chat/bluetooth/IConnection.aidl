// IConnection.aidl
package chat.literacyapp.org.chat.bluetooth;

// Declare any non-default types here with import statements

import chat.literacyapp.org.chat.bluetooth.IConnectionCallback;

interface IConnection {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   String getAddress();
   String getName();
   int startServer(in String packageName, in int maxConnections);
   int unregisterCallback(in String packageName);
   int registerCallback(in String packageName, IConnectionCallback cb);
   void shutdown(in String packageName);
   int connect(in String packageName, in String device);
}
