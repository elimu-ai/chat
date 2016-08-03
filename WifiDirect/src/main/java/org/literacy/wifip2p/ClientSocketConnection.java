package org.literacy.wifip2p;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class ClientSocketConnection extends Thread implements NotificationCenter.NotificationCenterDelegate {

    private static final String TAG = ClientSocketConnection.class.getSimpleName();
    private final int port;
    private Handler handler;
    private ConnectionManager chat;
    private String mAddress;

    public ClientSocketConnection(Handler handler, String groupOwnerAddress, int port) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress, port), 5000);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Launching the I/O handler");
            Log.d(TAG, "Launching the I/O handler");
            chat = new ConnectionManager(socket, handler, "Client");
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

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }
}
