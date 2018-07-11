package com.hb.pocket.client.thread;

import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by hb on 11/07/2018.
 */
public class ClientWriteThread extends ClientThreadParent {

    private static String TAG = ClientWriteThread.class.getSimpleName();

    private OutputStream outputStream = null;

    private PrintWriter printWriter = null;

    public ClientWriteThread(Socket socket, OutputStream outputStream) {
        super(socket);
        this.outputStream = outputStream;
        if (this.outputStream != null) {
            if (printWriter == null) {
                printWriter = new PrintWriter(outputStream);
            }
        }
    }

    /**
     * Send the data to the server.
     * @param msg
     * @return
     */
    public boolean sendMessage(String msg) {
        if (outputStream == null) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        String[] strArr = msg.split("\n");
        if (printWriter == null) {
            if (outputStream != null) {
                printWriter = new PrintWriter(outputStream);
                int i =0;
                for (i = 0; i < strArr.length - 1; i++) {
                    printWriter.write(strArr[i]);
                    printWriter.write("\n");
                }
                printWriter.write(strArr[i]);
                printWriter.write("\n");
                printWriter.flush();
                return true;
            }
        } else {
            int i =0;
            for (i = 0; i < strArr.length - 1; i++) {
                printWriter.write(strArr[i]);
                printWriter.write("\n");
            }
            printWriter.write(strArr[i]);
            printWriter.write("\n");
            printWriter.flush();
            return true;
        }
        return false;
    }

    /**
     * Close the write stream.
     * @return
     */
    public boolean close() {
        if (printWriter != null) {
            printWriter.close();
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        MyLog.i(TAG, "Client write thread close.");

        return shutDownParentSocket();
    }
}
