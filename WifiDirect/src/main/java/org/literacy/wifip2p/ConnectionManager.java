package org.literacy.wifip2p;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class ConnectionManager implements Runnable {

    private final String side;
    private Socket socket = null;
    private Handler handler;
    private static final String TAG = "ChatHandler";

    public ConnectionManager(Socket socket, Handler handler, String who) {
        this.socket = socket;
        this.handler = handler;
        this.side = who;
    }


    @Override
    public void run() {
        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!Thread.currentThread().isInterrupted()) {
                String messageStr = null;
                messageStr = input.readLine();
                if (messageStr != null) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Read from the stream: " + messageStr);
                    updateMessages(messageStr, false);
                } else {
                    break;
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMessages(String messageStr, boolean local) {
        Log.e(TAG, "Updating message: " + messageStr);
        if (local) {
            messageStr = "me: " + messageStr;
        } else {
            messageStr = "them: " + messageStr;
        }
        Log.d(TAG, "is local: " + local + " output:" + messageStr);

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", messageStr);

        Message message = new Message();
        message.setData(messageBundle);
        handler.sendMessage(message);
    }


}
