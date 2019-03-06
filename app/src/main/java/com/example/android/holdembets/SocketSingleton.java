package com.example.android.holdembets;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

// Singleton made for socket, so that there exists only one socket instance at one time, accessible from anywhere
public class SocketSingleton {
    private static Socket socket = null;

    public static Socket getInstance() {
        if (socket == null) {
            try {
                socket = IO.socket("http://10.0.2.2:3000/");

                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return socket;
    }
}