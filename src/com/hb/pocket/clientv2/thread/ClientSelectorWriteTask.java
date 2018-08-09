package com.hb.pocket.clientv2.thread;

import com.hb.pocket.clientv2.thread.callback.IClientSelectorWriteCallback;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by hb on 07/08/2018.
 */
public class ClientSelectorWriteTask implements Runnable {

    private static String TAG = ClientSelectorWriteTask.class.getSimpleName();

    private SocketChannel socketChannel;

    private String data;

    private IClientSelectorWriteCallback iClientSelectorWriteCallback;

    public ClientSelectorWriteTask(SocketChannel socketChannel, String data, IClientSelectorWriteCallback iClientSelectorWriteCallback) {
        this.socketChannel = socketChannel;
        this.data = data;
        this.iClientSelectorWriteCallback = iClientSelectorWriteCallback;
    }

    @Override
    public void run() {
        try {
            if (socketChannel.isConnected() && socketChannel.isOpen()) {
                if (iClientSelectorWriteCallback != null) {
                    iClientSelectorWriteCallback.onStartWrite();
                }
                if (write(socketChannel, data)) {
                    iClientSelectorWriteCallback.onEndWrite(true);
                } else {
                    iClientSelectorWriteCallback.onEndWrite(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the write.
     * @param channel
     * @param msg
     * @throws IOException
     */
    private boolean write(SocketChannel channel, String msg) throws IOException {
        try {
            String[] tmpArray = msg.split("\n");
            ByteBuffer[] bufferArray = new ByteBuffer[tmpArray.length];
            for (int i = 0; i < tmpArray.length; i++) {
                byte[] bytes = (tmpArray[i] + "\n").getBytes(Charset.forName("UTF-8"));
                ByteBuffer buffer = ByteBuffer.allocate(bytes.length); // Alloc heap buffer.
                buffer.put(bytes);
                buffer.flip();// Switch the read model.
                bufferArray[i] = buffer;
            }
            channel.write(bufferArray, 0, bufferArray.length);
            return true;
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
        }
        return false;
    }
}
