package com.example.dron;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    boolean isStart = false;

    DronIndicator indicator;
    Button btn_conn;
    Button btn_setting;
    Button btn_start;

    RelativeLayout layout_js_left, layout_js_right;
    View_JoyStick js_left, js_right;

    static public boolean joystick_mode;
    int[] joystick = {
            R.drawable.controller_left,
            R.drawable.controller_right,
            R.drawable.controller_right
    };

    ImageView[] iv_joystick;

    Timer nTimer, tTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        //BLE를 지원하지 않는 기기라면 토스트창 안내 후 종료시킨다.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "본 기기는 BLE를 지원하지 않습니다. 죄송합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = this;

        //롤리팝이상이면 권한물어봄
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // 블루투스 사용 권한 체크( 사용권한이 없을경우 -1 )
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을경우

                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // 사용자가 임의로 권한을 취소시킨 경우
                    // 권한 재요청
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                } else {
                    // 최초로 권한을 요청하는 경우(첫실행)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            } else {
                // 사용 권한이 있음을 확인한 경우
                //initLayout();
            }
        }

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(Fragment_Bluetooth.mDeviceAddress);
            Log.d("연결결과는", "Connect request result=" + result);
        }

        setDronestate();
        initView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // 갤러리 사용권한에 대한 콜백을 받음
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 동의 버튼 선택
                    //initLayout();
                } else {
                    // 사용자가 권한 동의를 안함
                    // 권한 동의안함 버튼 선택
                    Toast.makeText(MainActivity.this , "권한사용을 동의해주셔야 이용이 가능합니다." , Toast.LENGTH_LONG ).show();
                    finish();
                }
                return;
            }
            // 예외케이스
        }
    }

    @Override
    protected void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();

        //블루투스 연결되었을 때(드론과 연결 되었을 때)만 작업을 수행하도록 함
        if (Fragment_Bluetooth.mDeviceAddress != null) setSendDataTask();

        setJoystickMode(drone.getJoystickMode());
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


    }

    private void initView() {

        indicator = new DronIndicator(this);
        btn_conn = (Button) findViewById(R.id.btn_conn);
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_start = (Button) findViewById(R.id.btn_start);

        initJoyStick();
        setBtnEvent();
        indicator.setDronIndicator("35KM/H", "3", "N38.26 E134.23");

        TimeTask timeTask = new TimeTask();
        tTimer = new Timer();
        tTimer.schedule(timeTask, 500, 100);
    }

    private void initJoyStick() {

        iv_joystick = new ImageView[2];

        iv_joystick[0] = (ImageView) findViewById(R.id.img_contLeft);
        iv_joystick[1] = (ImageView) findViewById(R.id.img_contRight);

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

/*
                if(arg1.getAction() == MotionEvent.ACTION_DOWN ) {

                    int currentX, currentY;
                    RelativeLayout.LayoutParams plControl = (RelativeLayout.LayoutParams) layout_js_left.getLayoutParams();

                    currentX = plControl.leftMargin;
                    currentY = plControl.bottomMargin;

                    float x = arg1.getRawX();
                    float y = arg1.getRawY();
                        Log.d("절대 좌표 ", x + "  " + y);

                    // x = arg1.getX();
                    // y = arg1.getY();
                    //Log.d("상대 좌표 ", x + "  " + y);

                    Log.d("이전 마진", plControl.leftMargin + "  " + plControl.topMargin );
                    plControl.leftMargin = (int) x - 150;
                    plControl.topMargin = (int)y - 150;
                    //plControl.bottomMargin = 0;

                    Log.d("이후 좌표 ", plControl.leftMargin + "  " + plControl.topMargin);
                    layout_js_left.setLayoutParams(plControl);

                }*/

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

                if(arg1.getAction() == MotionEvent.ACTION_DOWN ) {
                    float x = arg1.getX();
                    float y = arg1.getY();
                    Log.d("좌표 ", x + "  " + y);

                }

                js_right.drawStick(arg1);
                return true;
            }
        });
    }

    /* 조이스틱 터치 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);

        if(event.getAction() == MotionEvent.ACTION_DOWN ){

            float x = event.getX();
            float y = event.getY();

            return true;
        }
        return false;
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

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_start.getText().toString().equals("START")) {
                    btn_start.setText("STOP");
                    isStart = false;
                }
                else if (btn_start.getText().toString().equals("STOP")) {
                    btn_start.setText("START");
                    isStart = true;
                }
            }
        });
    }

    public void setJoystickMode(int _mode) {

        switch (_mode){
            case 0 :
                iv_joystick[0].setImageResource(joystick[0]);
                iv_joystick[1].setImageResource(joystick[1]);
                break;

            case 1 :
                iv_joystick[0].setImageResource(joystick[1]);
                iv_joystick[1].setImageResource(joystick[0]);
                break;
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

            if(mBluetoothLeService == null) return;
            if(!isStart){
                sendText = rStartByte + EndByte + "";
                mBluetoothLeService.write(mDefaultChar,sendText.getBytes());
                return;
            }

            int left_x = js_left.getX(), left_y = js_left.getY(),
                    right_x = js_right.getX(), right_y = js_right.getY();

            if(left_x > 200) left_x = 200;
            else if(left_x < 0) left_x = 0;

            if(left_y > 200) left_y = 200;
            else if(left_y < 0) left_y = 0;

            if(right_x > 200) right_x = 200;
            else if(right_x < 0) right_x = 0;

            if(right_y > 200) right_y = 200;
            else if(right_y < 0) right_y = 0;

            if (MainActivity.joystick_mode) {
                sendText = StartByte + left_x +  left_y
                        +  right_x + right_y + EndByte +"";
            }
            sendText = StartByte + right_x + right_y
                    + left_x + left_y + EndByte+ "" ;


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
