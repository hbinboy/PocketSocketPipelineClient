package com.hb.pocket.clientv2.thread.callback;

/**
 * Created by hb on 07/08/2018.
 */
public interface IClientSelectorWriteCallback {
    public void onStartWrite();

    public void onEndWrite(boolean isSuccess);
}
