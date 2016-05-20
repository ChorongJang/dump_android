package com.example.dron.Connecting;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectingActivity extends AppCompatActivity implements Fragment_ConnectingMenu.OnMenuSelectedListener {

    int StartByte = 0xCB, EndByte = 0xCA;
    String title = "CONNECT & TRIMMING";

    TextView tv_title;
    ImageButton btn_back;
    ImageButton btn_save;

    FragmentManager manager;
    SubMenuFragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //권한을 갖고 있는지 점검하는 코드(마시멜로버전이상을 위한 코드임)
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        // BluetoothService 클래스 생성
        ((MainActivity)MainActivity.mContext).registerble();
        manager = getFragmentManager();

        initComponent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //애니메이션 효과 없애기
    @Override
    protected void onResume(){
        this.overridePendingTransition(0, 0);

        if (MainActivity.mBluetoothLeService != null) {
            final boolean result = MainActivity.mBluetoothLeService.connect(Fragment_Bluetooth.mDeviceAddress);
            Log.d("연결결과는", "Connect request result=" + result);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*initialize btn name and event*/
    private void initComponent() {

        setMenuFragment();

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_save = (ImageButton) findViewById(R.id.btn_save);

        tv_title.setText(title);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });

        btn_save.setVisibility(View.INVISIBLE);
    }

    private void setMenuFragment() {

        String[] menuTitle = {"Drone Select", "Bluetooth", "WiFi", "Trimming"};
        int[] iconRes = {R.drawable.connect_icon_dron, R.drawable.connect_icon_bluetooth,
                R.drawable.connect_icon_wifi, R.drawable.connect_icon_trimming};

        Fragment fragment = new Fragment_ConnectingMenu();
        Bundle   bundle   = new Bundle();
        bundle.putStringArray("menuTitle", menuTitle);
        bundle.putIntArray("iconRes", iconRes);

        fragment.setArguments(bundle);

        addFragment(fragment, null);
    }

    private void onClickBack(){

        int fragment_count = getFragmentManager().getBackStackEntryCount();

        if(fragment_count != 0) {
            manager.popBackStack();
            tv_title.setText(title);
            btn_save.setVisibility(View.INVISIBLE);
        }
        else finish();
    }

    private void onClickSave(){

        String data = "";
        // issue : data를 반환하도록 만들기
        data = activeFragment.setDroneInfo();

        if(MainActivity.mBluetoothLeService == null) return;
        String sendText = StartByte + ","+ data + "," + EndByte;

        Log.d("보내는 메시지는:", sendText);
        //MainActivity.mBluetoothLeService.write(mDefaultChar, "Dasd".getBytes()); --> mDefaultChar 이건 어쩌지...

    }

    @Override
    public void OnMenuSelected(int fragment_idx, String fragment_title) {

        SubMenuFragment fragment = null;
        int fragment_count = getFragmentManager().getBackStackEntryCount();

        if(fragment_count != 0) return;

        Log.e("fragment", "" + fragment_idx);
        switch (fragment_idx){

            case 0:
                fragment = new Fragment_DroneSelect();
                addFragment(fragment, fragment_title);
                break;

            case 1:
                //onClickBluetooth();
                fragment = new Fragment_Bluetooth();
                addFragment(fragment, "블루투스 연결설정");
                break;

            case 2: onClickWiFi();
                break;

            case 3:
                fragment = new Fragment_Trimming();
                addFragment(fragment, fragment_title);
                break;

            default:
                break;
        }
    }


    private void onClickWiFi(){
        /* 추후 구현 */
        Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
    }

//    private void onClickBluetooth(){
//
//        DeviceScanActivity deviceScanActivity = new DeviceScanActivity();
//        Intent intent = new Intent(this, DeviceScanActivity.class);
//        startActivity(intent);
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    private void addFragment(Fragment fragment, String fragment_title){

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frag_container, fragment);

        if(fragment_title != null ) {
            transaction.addToBackStack(null);
            tv_title.setText(fragment_title);

            btn_save.setVisibility(View.VISIBLE);
            activeFragment = (SubMenuFragment) fragment;
        }

        transaction.commit();
    }

}
