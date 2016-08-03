package org.literacy.wifip2p;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class GroupOwnerSocketConnection extends Thread implements NotificationCenter.NotificationCenterDelegate {
    private ServerSocket socket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";
    private ConnectionManager chat;

    public GroupOwnerSocketConnection(Handler handler, int port) throws IOException {
        try {
            socket = new ServerSocket(port);
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();

            throw e;
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                Socket s = socket.accept();
                Log.d(TAG, "Launching the I/O handler");
                chat = new ConnectionManager(s, handler, "Group");
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }
}
