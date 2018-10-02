package com.hb.pocket.clientv2.thread;

import com.hb.pocket.clientv2.thread.callback.IClientSelectorReadCallback;
import com.hb.pocket.data.Data;
import com.hb.pocket.data.DataManager;
import com.hb.utils.config.ClientConfig;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hb on 07/08/2018.
 */
public class ClientSelectorReadTask implements Runnable {

    private static String TAG = ClientSelectorReadTask.class.getSimpleName();

    private SocketChannel socketChannel;

    private IClientSelectorReadCallback iClientSelectorReadCallback;

    private Map<String, Data> mapData;

    public ClientSelectorReadTask(SocketChannel socketChannel, Map<String, Data> mapData, IClientSelectorReadCallback iClientSelectorReadCallback) {
        this.socketChannel = socketChannel;
        this.mapData = mapData;
        this.iClientSelectorReadCallback = iClientSelectorReadCallback;
    }

    @Override
    public void run() {
        try {
            if (socketChannel.isConnected() && socketChannel.isOpen()) {
                if (iClientSelectorReadCallback != null) {
                    iClientSelectorReadCallback.onStartRead();
                }
                MyLog.d(TAG, "Read the package length is: " + read(socketChannel, iClientSelectorReadCallback));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the read message that from the server.
     * @param channel
     * @param iClientSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int read(SocketChannel channel, IClientSelectorReadCallback iClientSelectorReadCallback) throws IOException {
        if (ClientConfig.readDataWithHeader) {
            return readDataWithHeader(channel, iClientSelectorReadCallback);
        } else {
            return readRawData(channel, iClientSelectorReadCallback);
        }
    }

    /**
     * Process the read message without Header {@link com.hb.pocket.data.header.Header}.
     * @param channel
     * @param iClientSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int readRawData(SocketChannel channel, IClientSelectorReadCallback iClientSelectorReadCallback) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int len = channel.read(buffer);
            if (len > 0) {
                MyLog.d(TAG, new String(buffer.array(), 0, len, Charset.forName("UTF-8"))); // buffer.array()：get the HeapByteFuffer raw data.
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

    /**
     * Process the read message with Header {@link com.hb.pocket.data.header.Header}.
     * @param channel
     * @param iClientSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int readDataWithHeader(SocketChannel channel, IClientSelectorReadCallback iClientSelectorReadCallback) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytes = buffer.array();
        try {
            int len = channel.read(buffer);
            int remainLen = len;
            int bodyLen = 0;
            int offset = 0;
            buffer.position(0);
            if (remainLen >= 0) {
                while (remainLen > 0) {
                    byte[] bufferBak = new byte[len - offset];
                    System.arraycopy(bytes, offset, bufferBak, 0, len - offset);
                    MyLog.d(TAG, "" + len);
                    DataManager dataManager = new DataManager();
                    if (len > 0) {
                        if (dataManager.getReceiveDataPackageData(bufferBak) != null) {
                            dataManager.getBody().getData();
                            offset += dataManager.getHeader().getHeadLen() + dataManager.getHeader().getDataLen();

                            remainLen = len - offset;
                            MyLog.d(TAG, "Split data display: "+ dataManager.getBody().getData()); // buffer.array()：get the HeapByteFuffer raw data.
                        }
                    }
                    if (iClientSelectorReadCallback != null && len > 0) {
                        String tmpMd5 = new String(dataManager.getHeader().getWholeMD5());
                        if (mapData.get(tmpMd5) == null) {
                            Data data = new Data();
                            data.setWholeMD5(tmpMd5);
                            mapData.put(tmpMd5, data);
                        }
                        if (mapData.get(tmpMd5).getHeaderMap().get(dataManager.getHeader().getIndexData()) == null) {
                            mapData.get(tmpMd5).getHeaderMap().put(dataManager.getHeader().getIndexData(), dataManager.getHeader());
                            mapData.get(tmpMd5).getBodyMap().put(dataManager.getHeader().getIndexData(), dataManager.getBody());
                        }
                        if (dataManager.getHeader().getCount() == mapData.get(tmpMd5).getHeaderMap().size()) {
                            iClientSelectorReadCallback.onEndRead(mapData.get(tmpMd5).getWholeData(), mapData.get(tmpMd5).getWholeData().length());
                            mapData.remove(tmpMd5);
                        }
                    } else {
                        iClientSelectorReadCallback.onEndRead(null, bodyLen);
                    }
                }
            } else {
                iClientSelectorReadCallback.onEndRead(null, -1);
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
