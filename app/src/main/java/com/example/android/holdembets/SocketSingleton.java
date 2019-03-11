// Stores sockets information. There can exist only one socket at a time, and it should be accessible everywhere.

package com.example.android.holdembets;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

// Singleton made for socket, so that there exists only one socket instance at one time, accessible from anywhere
public class SocketSingleton {
    private static Socket socket = null;

    public static Socket getInstance() {
        if (socket == null) {
            try {
                socket = IO.socket("https://evening-meadow-31771.herokuapp.com/");

                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return socket;
    }

    // Sends server a users information that is disconnecting, so it can be processed and disconnected from the servers side
    public static void disconnectUser(String roomKey, String username) {
        if (socket!=null) {
            socket.emit("disconnectwithdata", roomKey, username);
            socket = null;
        }
    }

    public static void disconnect() {
        if (socket!=null) {
            socket.disconnect();
            socket = null;
        }
    }
}
