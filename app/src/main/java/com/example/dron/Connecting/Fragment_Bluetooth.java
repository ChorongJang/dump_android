package com.example.dron.Connecting;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

import java.util.ArrayList;

/**
 * Created by chorong on 16. 4. 8..
 */
public class Fragment_Bluetooth extends SubMenuFragment {

    LinearLayout layout_bluetooth;  //해당레이아웃
    Button btn_bluetooth_search;   //검색버튼
    private LeDeviceListAdapter mLeDeviceListAdapter;  //검색되는 블루투스들이 ListView에 나타나게함
    private BluetoothAdapter mBluetoothAdapter;  //블루투스가 On/Off상태인지 파악
    private boolean mScanning;   //스캔중인지 아닌지 파악
    private Handler mHandler;   //핸들러
    public static String mDeviceName;           //검색 후 리스트뷰 한개 선택 시 기기이름저장
    public static String mDeviceAddress = null;    //검색 후 리스트뷰 한개 선택 시 기기주소저장

    private static final int REQUEST_ENABLE_BT = 1;             //블루투스가 가능한지 나타내는 static함수
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;               //10초동안 검색하게 함

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v){

        layout_bluetooth = (LinearLayout) v.findViewById (R.id.layout_bluetooth);
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 블루투스가 꺼져있으면 블루투스를 킬 것인가를 묻는다.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //Toast.makeText(getActivity(), "Bluetooth가 실행안되어있다", Toast.LENGTH_SHORT).show();
        }
        //BLE를 지원하지 않는 기기라면 토스트창 안내 후 종료시킨다.
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), "본 기기는 BLE를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        // 블루투스를 지원하지 않는 기기라면 토스트창 안내 후 종료시킨다.(가능성 거의없음)
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "본 기기는 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler = new Handler();
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        ListView list = (ListView)v.findViewById(R.id.listView);
        list.setAdapter(mLeDeviceListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("리스트한개클릭","성공");
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                //이전에 연결해 놓은 블루투스가 있다면 종료하고 새로운 블루투스 연결시도함
                if(mDeviceAddress != null) {   //이전 블루투스 연결을 끊고 저장되어있던 기기이름,주소 모두 초기화
                    MainActivity.mBluetoothLeService.disconnect();
                    MainActivity.mBluetoothLeService.close();
                    mDeviceAddress = null;
                    mDeviceName = null;
                }
                //리스트 한개를 클릭할 때 장치의 이름과 주소를 저장함.
                mDeviceAddress = device.getAddress();
                mDeviceName = device.getName();
                //연결
                MainActivity.mBluetoothLeService.connect(mDeviceAddress);
                Log.d(mDeviceName, "연결오케이");
                Toast.makeText(getActivity(), "연결되었습니다", Toast.LENGTH_SHORT).show();
                //리스트뷰 초기화
                mLeDeviceListAdapter.clear();
                btn_bluetooth_search.setText("블루투스 검색");
                //스캔중이면 스캔을 멈추게하고 스캔중이 아니라는 FALSE표시를 한다
                if (mScanning) {
                    Log.d("스캔중이구나?","그럼스캔을멈추자");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }

            }
        });

        btn_bluetooth_search = (Button) v.findViewById(R.id.btn_bluetooth_search);
        btn_bluetooth_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled()) {
                    if(btn_bluetooth_search.getText().toString().equals("블루투스 검색")) {
                        btn_bluetooth_search.setText("검색하기 중단");
                        scanLeDevice(true);
                    }
                    else if(btn_bluetooth_search.getText().toString().equals("검색하기 중단")) {
                        btn_bluetooth_search.setText("블루투스 검색");
                        scanLeDevice(false);
                    }
                }
                else{ Toast.makeText(getActivity(), "블루투스를 작동시켜주세요", Toast.LENGTH_SHORT).show();}
            }
        });

    }

    @Override
    public String setDroneInfo() {

        //Drone 변경
        return "true";

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("블루투스 연결시도","연결하겠다");
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //리스트 선택하거나 액티비티가 종료되면 리스트들을 초기화시키고 스캔을 멈춤
    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("스캔하고있니?","ㅇㅇ");
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    btn_bluetooth_search.setText("블루투스 검색");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getActivity().getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
