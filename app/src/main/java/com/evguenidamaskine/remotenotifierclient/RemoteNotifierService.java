package com.evguenidamaskine.remotenotifierclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class RemoteNotifierService extends Service {

    private final boolean bLOG = true;
    private final String sTAG = getClass().getSimpleName();
    private final int PORT = 10600;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private boolean bIsListening = false;

    private Thread mListenerThread = null;

    ServerSocket mListenerSocket = null;

    public RemoteNotifierService() {

    }

    @Override
    public void onCreate() {
        // TODO ???        super.onCreate();
        
        if (bLOG) {
            Log.i(sTAG, "onCreate");
        }


        HandlerThread thread = new HandlerThread("ServiceStartArguments",
            Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mListenerThread = new Thread(new ListenerThread());
    }

    class ListenerThread implements Runnable {
        @Override
        public void run() {
            try {
                mListenerSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                Log.i(sTAG, "Failed to create listener socket.");
                e.printStackTrace();
                return;
            }
//            doNotification("v2/ID1/ID2/TYPE/DATA/CONTENT/1/2/3");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (bLOG) {
                        Log.i(sTAG, "Listening..." + PORT);
                    }
                    Socket socket = mListenerSocket.accept();
                    // Read here, not expecting simultaneous connections
                    DataInputStream is = new DataInputStream(socket.getInputStream());
                    byte[] msgBytes = new byte[256];
                    int n = is.read(msgBytes);
                    String s = new String(msgBytes, 0, n);
                    mServiceHandler.post(new UIUpdater(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(sTAG, "Listener thread exiting.");
        }
    }


    /**
     * Start (and restart on configuration change).
     * Port, notification type, etc. are packed into Intent
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (bLOG) {
            Log.i(sTAG, "onStartCommand");
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.setData(intent.getBundleExtra("config"));
        mServiceHandler.handleMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListenerThread.interrupt(); // redundant?
        // This called but listener thread is still around (for a bit).
        if (bLOG) {
            Log.i(sTAG, "onDestroy");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            if (!bIsListening) {
                mListenerThread.start();
                bIsListening = true;
            }
        }
    }

    class UIUpdater implements Runnable {
        public UIUpdater(String s) {
            mMsg = s;
        }

        @Override
        public void run() {
            doNotification(mMsg);
        }

        private String mMsg;
    }


    // TODO: review!
    void doNotification(String msgRaw) {
        HashMap<String, String> message = new HashMap<String, String>();
        String[] msgTokens = msgRaw.split("/");
        int i = 0;
        message.put("Version", msgTokens[i++]);
        message.put("Device", msgTokens[i++]);
        message.put("Id", msgTokens[i++]);
        message.put("Type", msgTokens[i++]);
        message.put("Data", msgTokens[i++]);
        message.put("Contents", msgTokens[i++]);

        for (; i < msgTokens.length; i++) {
            message.put("Contents", message.get("Contents").concat(msgTokens[i]));
        }

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        nb.setContentTitle(message.get("Type"));
        String dataText = message.get("Data");
        dataText += " : ";
        dataText += message.get("Contents");
        nb.setContentText(dataText);
        nb.setSmallIcon(R.drawable.abc_ic_voice_search_api_mtrl_alpha); // TODO
        nb.setCategory(Notification.CATEGORY_CALL);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        nb.setSound(soundUri);

        NotificationManager nm =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(9999, nb.build());
    }

}
