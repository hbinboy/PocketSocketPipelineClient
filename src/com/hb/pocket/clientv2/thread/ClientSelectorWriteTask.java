package com.hb.pocket.clientv2.thread;

import com.hb.pocket.clientv2.thread.callback.IClientSelectorWriteCallback;
import com.hb.pocket.data.DataManager;
import com.hb.utils.config.ClientConfig;
import com.hb.utils.log.MyLog;

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
        if (ClientConfig.writeDataWithHeader) {
            return writeDataWithHeader(channel, msg);
        } else {
            return writeRawData(channel, msg);
        }
    }

    /**
     * Process the write message without Header {@link com.hb.pocket.data.header.Header}
     * @param channel
     * @param msg
     * @return
     * @throws IOException
     */
    private boolean writeRawData(SocketChannel channel, String msg) throws IOException {
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

    /**
     * Process the write message with Header {@link com.hb.pocket.data.header.Header}
     * @param channel
     * @param msg
     * @return
     * @throws IOException
     */
    private boolean writeDataWithHeader(SocketChannel channel, String msg) throws IOException {
        DataManager dataManager = new DataManager();
        String wholeMessageMD5 = dataManager.md5(msg);
        String[] result = dataManager.spliteString(msg);

        try {
            for (int i = 0; i < result.length; i++) {
                byte[] data = dataManager.genSendDataPackage(result[i] + '\n', i, result.length, wholeMessageMD5);
                ByteBuffer buffer = ByteBuffer.allocate(data.length); // Alloc heap buffer.
                buffer.put(data);
                buffer.flip();// Switch the read model.
                MyLog.d(TAG, "" + channel.write(buffer/*, 0, bufferArray.length*/));
            }
            return true;
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
        }

        return false;
    }
}
