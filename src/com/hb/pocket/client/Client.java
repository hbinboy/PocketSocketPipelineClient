package com.hb.pocket.client;

import com.hb.pocket.client.manager.ClientThreadManager;
import com.hb.pocket.client.thread.ClientThreadStatus;
import com.hb.utils.log.MyLog;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hb on 11/07/2018.
 */
public class Client implements Runnable {

    private static String TAG = Client.class.getSimpleName();

    private Thread thread;

    private ClientThreadManager clientThreadManager = null;

    /**
     * Current thread need to action order.
     */
    private volatile ClientThreadStatus order;

    /**
     * The client socket thread is shutdown or not.
     */
    private boolean shutDown = false;

    /**
     * The run is first start or not.
     */
    private boolean isFirstStart = true;

    /**
     * The object lock.
     */
    private AtomicBoolean object = new AtomicBoolean(false);

    /**
     * The socket need to send data.
     */
    private String msg = "";

    public Client() {
        thread = new Thread(this);
        clientThreadManager = new ClientThreadManager();
    }

    /**
     * Start the client sokcet thread.
     */
    public void start() {
        thread.start();
    }

    /**
     * Connect to the server.
     * @return
     */
    private boolean connect() {
        return clientThreadManager.connect();
    }

    /**
     * Send the message to the server.
     * @param msg
     * @return
     */
    private boolean sendMessage(String msg) {
        return clientThreadManager.sendMessage(msg);
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    /**
     * Close the client resource.
     * @return
     */
    private boolean close() {
        return clientThreadManager.close();
    }

    @Override
    public void run() {
        MyLog.i(TAG, "Client run is started.");
        synchronized (object) {
            while (!shutDown) {
                MyLog.i(TAG, "Client enter while loop.");
                if (order == ClientThreadStatus.CONNECT || isFirstStart == true) {
                    if (!connect()) {
                        MyLog.i(TAG, "Client connect scerver failed.");
                        isFirstStart = true;
                        break;
                    } else {
                        isFirstStart = false;
                    }
                }
                if (order == ClientThreadStatus.SENDMSG) {
                    if (sendMessage(msg)) {
                        MyLog.i(TAG, "Client sendmessage success.");
                    } else {
                        MyLog.i(TAG, "Client sendmessage failed.");
                    }
                    order = ClientThreadStatus.IDEL;
                }
                if (order == ClientThreadStatus.CLOSE) {
                    close();
                    order = ClientThreadStatus.IDEL;
                    isFirstStart = true;
                    break;
                }
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
            MyLog.i(TAG, "Client thread is finish!");
        }
    }

    /**
     * Set the order.
     * @param clientThreadStatus
     */
    public void setOrder(ClientThreadStatus clientThreadStatus) {
        synchronized (object) {
            order = clientThreadStatus;
            object.notify();
        }
    }

    /**
     * Get the current
     * @return
     */
    public boolean isShutDown() {
        return shutDown;
    }
}
