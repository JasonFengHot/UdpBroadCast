package cn.ismartv.huibin.udpbroadcast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
    static {
        System.loadLibrary("native-lib");
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(){
            @Override
            public void run() {

                startUdpBroadcastServer();
            }
        }.start();
    }

    public native void startUdpBroadcastServer();
}
