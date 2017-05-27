package mo.ipm.mengl.banphone;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import java.util.HashSet;
import java.util.List;

/**
 * Created by laicm on 25/5/2017.
 */


public class BLEService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    private ScreenObserver mScreenObserver;
    private ScanResult scanResult;
    private Vibrator myVibrator;

    private HashSet<String> selectAddress = new HashSet<String>();

    private final IBinder mBinder = new LocalBinder();

    private boolean open = true;

    private int rssi = -100000;
    private int setRssi = 0;

    @Override
    public void onCreate() {
        open = true;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        setRssi = intent.getIntExtra("RSSI", 0) * -10;
        Log.d("SSS", String.valueOf(setRssi));
        Log.d("Still", "Work1");
        start();
        startMonitor();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        open = false;
        mBluetoothAdapter=null;
        mScreenObserver.shutdownObserver();
        myVibrator.cancel();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class LocalBinder extends Binder {

    }

    private void start() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SelectDevices", Context.MODE_PRIVATE);
        selectAddress = (HashSet<String>) sharedPreferences.getStringSet("address", new HashSet<String>());


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        startScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (scanResult != null && open) {
                            Log.d("RSSI", String.valueOf(scanResult.getRssi()));
                            Log.d("RSSI", "UP");
                            rssi = scanResult.getRssi();
                        }
                        Thread.sleep(100);
                    }
                } catch (Exception e) {

                }

            }
        }).start();
    }

    private void startScan() {
        mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
    }

    private ScanCallback mScanCallback =
            new ScanCallback() {
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    for (String s : selectAddress) {
                        if (s.equals(result.getDevice().getAddress())) {
                            scanResult = result;
                        }
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    for(ScanResult r:results) {
                        for (String s : selectAddress) {
                            if (s.equals(r.getDevice().getAddress())) {
                                scanResult = r;
                            }
                        }
                    }
                }
            };



    private void startMonitor(){
        new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mScreenObserver = new ScreenObserver(getApplication());
        mScreenObserver.startObserver(new ScreenObserver.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.d("ScreenStatus", "ScreenOn");
            }

            @Override
            public void onScreenOff() {
                myVibrator.cancel();
                Log.d("ScreenStatus", "ScreenOff");
            }

            @Override
            public void onUserPresent() {
                if(rssi>=setRssi){
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            getApplication(), 0, new Intent(getApplication(), MainActivity.class), 0);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplication())
                                    .setContentTitle(getString(R.string.contentTitle))
                                    .setContentText(getString(R.string.contentText))
                                    .setSmallIcon(R.drawable.ic_car_repair)
                                    .setFullScreenIntent(contentIntent,false);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());
                    myVibrator.vibrate(new long[]{500,1000},0);
                    Log.d("Reaction","IN RANGE");
                }else{
                    myVibrator.cancel();
                    Log.d("Reaction","NOT IN RANGE");
                }
                Log.d("ScreenStatus", "UserPresent");
            }

        });
    }

}

