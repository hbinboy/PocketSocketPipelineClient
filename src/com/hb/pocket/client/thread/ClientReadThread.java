package com.hb.pocket.client.thread;

import com.hb.pocket.client.io.MyBufferedReader;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hb on 11/07/2018.
 */
public class ClientReadThread extends ClientThreadParent {

    private static String TAG = ClientReadThread.class.getSimpleName();

    private InputStream inputStream = null;

    /**
     * Write data to client socket.
     */
    private InputStreamReader inputStreamReader = null;

    /**
     * Reader.
     */
    private MyBufferedReader myBufferedReader = null;

    /**
     * Object lock.
     */
    private AtomicBoolean object = new AtomicBoolean(false);

    /**
     * The client socket is closed or not.
     */
    private boolean shutDown = false;

    public ClientReadThread(Socket socket) {
        super(socket);
    }

    /**
     * Close the read stream.
     * @return
     */
    public boolean close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        MyLog.i(TAG, "Client read thread close.");
        return true;
    }

    @Override
    public void run() {
        String info = null;
        MyLog.i(TAG, "Client read thread run is started.");
        synchronized (object) {
            while (!shutDown) {
                MyLog.i(TAG, "Client read thread enter while loop.");
                // Init the inputstream objet.
                if (inputStream == null) {
                    try {
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Init the inputStreamReader object.
                if (inputStreamReader == null && inputStream != null) {
                    inputStreamReader = new InputStreamReader(inputStream);
                }
                // Init the myBufferedReader object.
                if (myBufferedReader == null && inputStreamReader != null) {
                    myBufferedReader = new MyBufferedReader(inputStreamReader);
                }
                // Read the data from client socket, if empty, wait.
                try {
                    info = myBufferedReader.readLine1(true);
                    while(info !=null){
                        MyLog.i(TAG, info);
                        info = myBufferedReader.readLine1(true);
                    }
                    shutDown = true;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    shutDown = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    shutDown = true;
                    break;
                }
            }
        }
        shutDown = true;
        MyLog.i(TAG, "Client read thread exit.");
    }
}
