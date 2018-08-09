package com.hb.pocket.clientv2.thread.callback;

/**
 * Created by hb on 07/08/2018.
 */
public interface IClientSelectorReadCallback {
    public void onStartRead();

    public void onEndRead(String data, int length);
}
