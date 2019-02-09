package com.iogate.helper;

import java.net.InetAddress;

public class IogateDev {

    public String mDevName;
    public InetAddress mDevIpAddress;
    public int mDevPort;

    IogateDev(InetAddress in_devIp, int in_devPort) {
        mDevName = "iogate";
        mDevIpAddress = in_devIp;
        mDevPort = in_devPort;
    }

    public InetAddress gstIpAddress() {
        return mDevIpAddress;
    }

    public int getPort() {
        return mDevPort;
    }

    @Override
    public String toString() {
        return mDevIpAddress + ":" + mDevPort;
    }
}
