package com.iogate.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

public class IogateHelper implements Runnable {

    static String TAG = "IogateHelper";
    static String SERVICE_TYPE = "_iogate._tcp.";
    String mServiceName = "iogate";

    Thread mHelperThr;

    NsdManager mNsdManager;
    NsdManager.DiscoveryListener  mDiscoveryListener;
    NsdManager.ResolveListener mResolveListener;
    NsdServiceInfo mService;

    IogateDev mIoGate = null;

    @Override
    public void run() {
        Log.d(TAG, "IOGate Starting...");
        //initializeDiscoveryListener();
        //mNsdManager.discoverServices(
        //        SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

        try {
            Thread.sleep(1000);
            showToast("Helper Started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "IOGate End thread");
    }

    public IogateHelper(Context context) {
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeDiscoveryListener() {

        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                    mNsdManager.resolveService(service, mResolveListener);

                } else if (service.getServiceName().contains(mServiceName)){
                    mNsdManager.resolveService(service, mResolveListener);
                    Log.d(TAG, "Contains machine: " + mServiceName);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
                if (serviceInfo.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same IP.");
                    mIoGate = new IogateDev(serviceInfo.getHost(), serviceInfo.getPort());
                    showToast("Found IOGate at: " + mIoGate);
                    stopDiscovery();
                    return;
                }
                mService = serviceInfo;
            }
        };
    }

    public void discoverServices() {
        stopDiscovery();  // Cancel any existing discovery request
        initializeDiscoveryListener();
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }

    public void showToast(final String in_strToast) {
        MainActivity.getMainUi().getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.getMainUi(),in_strToast,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
