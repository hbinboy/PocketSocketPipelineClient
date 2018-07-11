package com.hb.pocket.client.manager;

import com.hb.pocket.client.thread.ClientReadThread;
import com.hb.pocket.client.thread.ClientWriteThread;
import com.hb.utils.config.ClientConfig;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.net.Socket;

/**
 * The socket client manager.
 * Created by hb on 11/07/2018.
 */
public class ClientThreadManager {

    private static String TAG = ClientThreadManager.class.getSimpleName();

    /**
     * The client socket.
     */
    private Socket socket = null;

    /**
     * The read thread.
     */
    private ClientReadThread clientReadThread = null;

    /**
     * The wirte thread.
     */
    private ClientWriteThread clientWriteThread = null;

    private boolean isStart = false;

    public ClientThreadManager() {
        isStart = false;
    }

    /**
     * Connect to the server.
     * @return
     */
    public boolean connect() {
        if (socket == null) {
            try {
                socket = new Socket(ClientConfig.ip, ClientConfig.port);
                socket.setKeepAlive(true);
                clientReadThread = new ClientReadThread(socket);
                clientReadThread.start();
                clientWriteThread = new ClientWriteThread(socket, socket.getOutputStream());
                clientWriteThread.start();
                isStart = true;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                socket = null;
                return false;
            }
        }
        return false;
    }

    /**
     * Close the client resource.
     * @return
     */
    public boolean close() {
        if (!clientReadThread.close()) {
            MyLog.e(TAG, "Close the clientreadthread failed!");
            return false;
        }
        if (!clientWriteThread.close()) {
            MyLog.e(TAG, "Close the clientWriteThread failed!");
            return false;
        }
        isStart = false;
        MyLog.i(TAG, "Client thread manager close.");
        return true;
    }

    /**
     * Send the data to the server.
     * @param msg
     * @return
     */
    public boolean sendMessage(String msg) {
        return clientWriteThread.sendMessage(msg);
    }

    public boolean isStart() {
        return isStart;
    }
}
