package org.literacy.wifip2p;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class GroupOwnerSocketConnection extends Thread {
    private final int messageRead;
    private final int myHandle;
    ServerSocket socket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";

    public GroupOwnerSocketConnection(Handler handler, int myHandle, int messageRead) throws IOException {
        try {
            socket = new ServerSocket(4545);
            this.handler = handler;
            this.myHandle = myHandle;
            this.messageRead = messageRead;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
                pool.execute(new ConnectionManager(socket.accept(), handler, myHandle, messageRead));
                Log.d(TAG, "Launching the I/O handler");
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {
                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }
}
