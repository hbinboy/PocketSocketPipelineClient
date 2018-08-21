package com.hb.utils.config;

/**
 * Created by hb on 11/07/2018.
 */
public class ClientConfig {

    /**
     * The server ip.
     */
    public static String ip = "10.250.11.43";

    /**
     * The server listening port.
     */
    public static final int port = 7909;

    /**
     * Receive the data with Header {@link com.hb.pocket.data.header.Header}
     */
    public static boolean readDataWithHeader = true;

    /**
     * Send the data with Header {@link com.hb.pocket.data.header.Header}
     */
    public static boolean writeDataWithHeader = true;
}
