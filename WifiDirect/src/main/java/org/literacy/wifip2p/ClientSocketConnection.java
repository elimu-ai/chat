package org.literacy.wifip2p;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class ClientSocketConnection extends Thread {

    private static final String TAG = ClientSocketConnection.class.getSimpleName();
    private final int port;
    private final int myHandle;
    private final int messageRead;
    private Handler handler;
    private ConnectionManager chat;
    private InetAddress mAddress;

    public ClientSocketConnection(Handler handler, InetAddress groupOwnerAddress, int port, int myHandle, int messageRead) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.port = port;
        this.myHandle = myHandle;
        this.messageRead = messageRead;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(), port), 5000);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ConnectionManager(socket, handler, myHandle, messageRead);
            new Thread(chat).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    public ConnectionManager getChat() {
        return chat;
    }
}
