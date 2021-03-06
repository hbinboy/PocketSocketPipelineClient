package com.hb.pocket.clientv2;

import com.hb.pocket.clientv2.thread.ClientSelectorReadTask;
import com.hb.pocket.clientv2.thread.ClientSelectorWriteTask;
import com.hb.pocket.clientv2.thread.callback.IClientSelectorReadCallback;
import com.hb.pocket.clientv2.thread.callback.IClientSelectorWriteCallback;
import com.hb.pocket.data.Data;
import com.hb.utils.config.ClientConfig;
import com.hb.utils.log.MyLog;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hb on 07/08/2018.
 */
public class Client implements Runnable {

    private static String TAG = Client.class.getSimpleName();

    private SelectorProvider selectorProvider = null;

    private Selector selector = null;

    private SocketChannel channel = null;

    private Thread thread;

    private boolean isStart = false;

    private ConcurrentHashMap<SelectionKey, Map<String, Data>> selectionKeySelectionKeyBack = new ConcurrentHashMap<>();

    private String ip = "";

    private int port = -1;

    ThreadPoolExecutor threadReadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 10, Integer.MAX_VALUE, 5,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    ThreadPoolExecutor threadWritePoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 10, Integer.MAX_VALUE, 5,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public Client() {

    }

    public boolean init() {
        if (!initSelector()) {
            return false;
        }
        if (!initSocketChannel()) {
            return false;
        }
        return true;
    }

    private boolean initSelector() {
        selectorProvider = SelectorProvider.provider();
        if (selectorProvider == null) {
            return false;
        }
        try {
            selector = selectorProvider.openSelector();
            if (selector == null) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initSocketChannel() {
        try {
            channel = selectorProvider.openSocketChannel();
            if (channel == null) {
                return false;
            }
            channel.configureBlocking(false);

        } catch (ClosedChannelException e) {
            MyLog.e(TAG, "Can not init the client.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Connect to server.
     * @return
     */
    public boolean connect() {
        try {
            channel.register(selector, SelectionKey.OP_CONNECT);
            if (ip != null && !ip.equals("") && port > 0) {
                if (channel.connect(new InetSocketAddress(ip, port))) {
                    MyLog.e(TAG, "Can not connection server.");
                    return false;
                }
            } else {
                if (channel.connect(new InetSocketAddress(ClientConfig.ip, ClientConfig.port))) {
                    MyLog.e(TAG, "Can not connection server.");
                    return false;
                }
            }
        } catch (IOException e) {
            MyLog.e(TAG, "Can not connection server.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the client and the listener.
     * @throws IOException
     */
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        if (selector != null) {
            selector.close();
            selector = null;
        }
        if (threadReadPoolExecutor != null) {
            threadReadPoolExecutor.shutdown();
        }
        if (threadWritePoolExecutor != null) {
            threadWritePoolExecutor.shutdown();
        }
        isStart = false;
    }
    @Override
    public void run() {
        try {
            while (isStart) {
                selector.select();
                if (selector != null) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKeySelectionKeyBack.containsKey(selectionKey)) {
                            continue;
                        }
                        handle(selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isStart = false;
    }

    private void handle(SelectionKey selectionKey) {
        if (selectionKey.isValid() && selectionKey.isConnectable()) {
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            try {
                if (sc.finishConnect()) {
                    // read
                    sc.register(selector, SelectionKey.OP_READ);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (selectionKey.isValid() && selectionKey.isReadable()) {
            MyLog.i(TAG, "Read start...");
            Map<String, Data> stringDataMap = new ConcurrentHashMap<>();
            selectionKeySelectionKeyBack.put(selectionKey, stringDataMap);
            threadReadPoolExecutor.execute(new ClientSelectorReadTask(channel, stringDataMap, new IClientSelectorReadCallback() {
                @Override
                public void onStartRead() {

                }

                @Override
                public void onEndRead(String data, int length) {
                    if (length >= 0) {
                        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ); // Listen the write modle,
                        MyLog.i(TAG, "The whole data display: "+ data);
                    } else if (length < 0) {
                        try {
                            close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        MyLog.i(TAG, "Close...");
                    }
                    selectionKeySelectionKeyBack.remove(selectionKey);
                }
            }));
        }
        if (selectionKey.isValid() && selectionKey.isWritable()) {
            MyLog.d(TAG, "Write start...");
            Map<String, Data> stringDataMap = new ConcurrentHashMap<>();
            selectionKeySelectionKeyBack.put(selectionKey, stringDataMap);
            threadWritePoolExecutor.execute(new ClientSelectorWriteTask(channel,"" + "\n",  new IClientSelectorWriteCallback() {
                @Override
                public void onStartWrite() {

                }

                @Override
                public void onEndWrite(boolean isSuccess) {
                    selectionKeySelectionKeyBack.remove(selectionKey);
                }
            }));
            // Cancel the write model, otherwise , the selector notice the write is already reaptly.
//            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);
        }
    }

    /**
     * Start the client thread.
     */
    public void startLoop() {
        if (thread == null) {
            thread = new Thread(this);
            isStart = true;
            thread.start();
        }
    }

    /**
     * Process the write.
     * @param msg
     * @throws IOException
     */
    public void sendMessage(String msg) throws IOException {
        threadWritePoolExecutor.execute(new ClientSelectorWriteTask(channel, msg + "\n", new IClientSelectorWriteCallback() {
            @Override
            public void onStartWrite() {

            }

            @Override
            public void onEndWrite(boolean isSuccess) {

            }
        }));
    }

    public boolean isStart(){
        return isStart;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
