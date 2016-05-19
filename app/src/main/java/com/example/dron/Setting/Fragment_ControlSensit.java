package com.example.dron.Setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

public class Fragment_ControlSensit extends SubMenuFragment {

    ToggleButton[] controlMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_controlsensiv, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

        int _mode = MainActivity.drone.getControlSensitivity();

        controlMode = new ToggleButton[3];

        controlMode[0] = (ToggleButton) v.findViewById(R.id.tog_easyMode);
        controlMode[1] = (ToggleButton) v.findViewById(R.id.tog_normalMode);
        controlMode[2] = (ToggleButton) v.findViewById(R.id.tog_sportMode);

        controlMode[_mode].setChecked(true);

        for(int i=0; i<3; i++) {
            controlMode[i].setOnClickListener(mClickListner);
        }
    }

    ToggleButton.OnClickListener mClickListner = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tog_easyMode:
                    setToggMode(0,1,2);
                    break;

                case R.id.tog_normalMode:
                    setToggMode(1,0,2);
                    break;

                case R.id.tog_sportMode:
                    setToggMode(2,0,1);
                    break;

            }
        }
    };

    private void setToggMode(int _s, int _m, int _n){
        controlMode[_s].setChecked(true);
        controlMode[_m].setChecked(false);
        controlMode[_n].setChecked(false);

        MainActivity.drone.setControlSensitivity(_s);
    }

    @Override
    public String setDroneInfo() {
        return "수정된 값";
    }
}
