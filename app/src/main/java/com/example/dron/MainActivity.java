package com.example.dron;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.dron.Component.DronIndicator;
import com.example.dron.Component.View_JoyStick;
import com.example.dron.Connecting.BluetoothLeService;
import com.example.dron.Connecting.ConnectingActivity;
import com.example.dron.Connecting.Fragment_Bluetooth;
import com.example.dron.Setting.SettingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static DroneState drone;

    public static Context mContext;
    int StartByte = 0xC9, EndByte = 0xCA;

    int rStartByte = 0xCB;

    DronIndicator indicator;
    Button btn_conn;
    Button btn_setting;

    RelativeLayout layout_js_left, layout_js_right;
    View_JoyStick js_left, js_right;

    static public boolean joystick_mode;

    Timer nTimer, tTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(Fragment_Bluetooth.mDeviceAddress);
            Log.d("연결결과는", "Connect request result=" + result);
        }

        setDronestate();
        initView();
    }

    @Override
    protected void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();

        //블루투스 연결되었을 때(드론과 연결 되었을 때)만 작업을 수행하도록 함
        if (Fragment_Bluetooth.mDeviceAddress != null) setSendDataTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //블루투스 연결되었을 때(드론과 연결 되었을 때)만 작업을 수행하도록 함
        if (Fragment_Bluetooth.mDeviceAddress != null) nTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drone.saveDroneState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        tTimer.cancel();

        // issue : destroy 할때 연결된 디바이스 값도 초기화 해줘야함, 종료했다가 다시 실행시 바로 연결 됨

        try {
            unregisterReceiver(mGattUpdateReceiver);
            unbindService(mServiceConnection);
        } catch (Exception e) {
            Log.e("destroy", "error");
        }
        mBluetoothLeService = null;
        Fragment_Bluetooth.mDeviceName = null;
        Fragment_Bluetooth.mDeviceAddress = null;

        //블루투스 연결되었을 때(드론과 연결 되었을 때)만 작업을 수행하도록 함
        if (Fragment_Bluetooth.mDeviceAddress != null) nTimer.cancel();
    }


    ///////블루투스////////
    public static BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private ArrayList<BluetoothGattCharacteristic> mWritableCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
    private boolean mConnected = false;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGattCharacteristic mDefaultChar = null;
    private String sendText = "";
    private BluetoothGatt mBluetoothGatt = null;
    // Code to manage Service lifecycle.


    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentNamFe, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService.initialize();
            if (!mBluetoothLeService.initialize()) {
                Log.e("메인액티비티블루투스서비스", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(Fragment_Bluetooth.mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i("ON GATT CONNECTED 1", "OK");
                mConnected = true;
                //  updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                Log.i("ON GATT CONNECTED 2", "OK");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //  updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                // clearUI();
                Log.i("ON GATT DISCONNECTED", "OK");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.i("ON GATT SERVICED IS COVERED", "OK");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                Log.i("ON DISPLAYED SERVICES", "OK");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i("ON GATT DATA AVAILABLE", "OK");
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        Log.d("데이터 왔냐..그럼 텍스트 바꿔라", data);
        if (data != null) {

            // if(rStartByte)

            indicator.setDronIndicator(data, "2", data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    //이용가능한 gatt를 찾아줌..
    private int displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return -1;
        String uuid = null;
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, "iBeacon");
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);

                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, "iBeacon Char");
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                Log.d("# GATT Char: ", gattCharacteristic.toString());
                //받자
                mBluetoothLeService.readCharacteristic(gattCharacteristic);
                //보내자
                mDefaultChar = gattCharacteristic;
                //변화가 있으면 바로 고고씽
                mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        return mWritableCharacteristics.size();
    }

    private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public void registerble() {

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    public void setDronestate() {

        String sfName = "DroneStateFile";
        SharedPreferences mPref;

        mPref = getSharedPreferences(sfName, 0);
        drone = new DroneState(mPref);

        drone.getJoystickMode();
    }

    private void initView() {

        indicator = new DronIndicator(this);
        btn_conn = (Button) findViewById(R.id.btn_conn);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        initJoyStick();
        setBtnEvent();
        indicator.setDronIndicator("35KM/H", "3", "N38.26 E134.23");

        TimeTask timeTask = new TimeTask();
        tTimer = new Timer();
        tTimer.schedule(timeTask, 500, 100);
    }

    private void initJoyStick() {

        layout_js_left = (RelativeLayout) findViewById(R.id.joystick_left);
        layout_js_right = (RelativeLayout) findViewById(R.id.joystick_right);

        js_left = new View_JoyStick(getApplicationContext(), layout_js_left, R.drawable.image_button);
        js_right = new View_JoyStick(getApplicationContext(), layout_js_right, R.drawable.image_button);

        setJoyStick();
    }

    private void setJoyStick() {

        joystick_mode = true;

        js_left.setStickSize(120, 120);
        js_left.setLayoutAlpha(150);
        js_left.setStickAlpha(100);
        js_left.setOffset(90);
        js_left.setMinimumDistance(50);

        layout_js_left.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js_left.drawStick(arg1);
                return true;
            }
        });

        js_right.setStickSize(120, 120);
        js_right.setLayoutAlpha(150);
        js_right.setStickAlpha(100);
        js_right.setOffset(90);
        js_right.setMinimumDistance(50);

        layout_js_right.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js_right.drawStick(arg1);
                return true;
            }
        });
    }

    private void setSendDataTask() {
        DroneTask droneTask = new DroneTask();
        nTimer = new Timer();
        nTimer.schedule(droneTask, 500, 100);
    }

    private void setBtnEvent() {
        btn_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();

                Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                startActivity(intent);
            }
        });

        btn_conn.setOnTouchListener(new View.OnTouchListener() { //버튼 터치시 이벤트
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                    btn_conn.setBackgroundResource(R.drawable.connect1);

                if (event.getAction() == MotionEvent.ACTION_UP) { //버튼에서 손을 떼었을 때
                    btn_conn.setBackgroundResource(R.drawable.connect);
                }
                return false;
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btn_setting.setOnTouchListener(new View.OnTouchListener() { //버튼 터치시 이벤트
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                    btn_setting.setBackgroundResource(R.drawable.setting1);
                if (event.getAction() == MotionEvent.ACTION_UP) { //버튼에서 손을 떼었을 때
                    btn_setting.setBackgroundResource(R.drawable.setting);
                }
                return false;
            }
        });
    }

    public void setJoystickMode(boolean _mode) {

        ImageView img_right = (ImageView) this.findViewById(R.id.img_contRight);
        ImageView img_left = (ImageView) this.findViewById(R.id.img_contRight);

        joystick_mode = _mode;

        if (joystick_mode) {
            img_right.setImageResource(R.drawable.controller_right);
            img_left.setImageResource(R.drawable.controller_left);
        } else {

            img_right.setImageResource(R.drawable.controller_left);
            img_left.setImageResource(R.drawable.controller_right);

        }
    }

    /* Timer for Drone Navigation bar && Pilot Drone */

    private Handler mHandler = new Handler();
    private Handler nHandler = new Handler();

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            indicator.setCurrentTime();
        }
    };

    private Runnable nSendJoyStickTask = new Runnable() {
        @Override
        public void run() {

            if (mBluetoothLeService == null) return;

            if (MainActivity.joystick_mode) {
                sendText = StartByte + "," + js_left.getX() + "," + js_left.getY()
                        + "," + js_right.getX() + "," + js_right.getY() + "," + EndByte;
            }
            sendText = StartByte + "," + js_right.getX() + "," + js_right.getY() +
                    js_left.getX() + "," + js_left.getY() + "," + "," + EndByte;


        Log.d("보내는 메시지는:",sendText);
        mBluetoothLeService.write(mDefaultChar,sendText.getBytes());
        }
    };

    class DroneTask extends TimerTask{
        public void run(){

            nHandler.post(nSendJoyStickTask);
        }
    }

    class TimeTask extends TimerTask{
        public void run(){
            mHandler.post(mUpdateTimeTask);
        }
    }

}
