package com.hb.pocket.clientv2.thread;

import com.hb.pocket.clientv2.thread.callback.IClientSelectorReadCallback;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by hb on 07/08/2018.
 */
public class ClientSelectorReadTask implements Runnable {

    private static String TAG = ClientSelectorReadTask.class.getSimpleName();

    private SocketChannel socketChannel;

    private IClientSelectorReadCallback iClientSelectorReadCallback;

    public ClientSelectorReadTask(SocketChannel socketChannel, IClientSelectorReadCallback iClientSelectorReadCallback) {
        this.socketChannel = socketChannel;
        this.iClientSelectorReadCallback = iClientSelectorReadCallback;
    }

    @Override
    public void run() {
        try {
            if (socketChannel.isConnected() && socketChannel.isOpen()) {
                if (iClientSelectorReadCallback != null) {
                    iClientSelectorReadCallback.onStartRead();
                }
                read(socketChannel, iClientSelectorReadCallback);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the read.
     * @param channel
     * @param iClientSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int read(SocketChannel channel, IClientSelectorReadCallback iClientSelectorReadCallback) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int len = channel.read(buffer);
            if (len > 0) {
                MyLog.i(TAG, new String(buffer.array(), 0, len, Charset.forName("UTF-8"))); // buffer.array()：get the HeapByteFuffer raw data.
            }
            if (iClientSelectorReadCallback != null && len > 0) {
                iClientSelectorReadCallback.onEndRead(new String(buffer.array(), 0, len, Charset.forName("UTF-8")), len);
            } else {
                iClientSelectorReadCallback.onEndRead(null, len);
            }
            return len;
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
            if (iClientSelectorReadCallback != null) {
                iClientSelectorReadCallback.onEndRead(null, -1);
            }
            return -1;
        }
    }
}
