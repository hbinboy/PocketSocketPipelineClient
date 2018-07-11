package com.hb.pocket.client.thread;

import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by hb on 11/07/2018.
 */
public class ClientThreadParent extends Thread {

    private static String TAG = ClientThreadParent.class.getSimpleName();

    protected Socket socket = null;

    public ClientThreadParent(Socket socket) {
        this.socket = socket;
        try {
            this.socket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the client socket.
     * @return
     */
    public boolean shutDownParentSocket() {
        try {
            socket.close();
            MyLog.i(TAG, "Client thread parent close.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
