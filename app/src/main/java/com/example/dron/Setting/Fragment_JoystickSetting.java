package com.example.dron.Setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

public class Fragment_JoystickSetting extends SubMenuFragment {

    ToggleButton tog_joystick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_joysticksetting, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

        tog_joystick = (ToggleButton) v.findViewById(R.id.tog_joystick);


        tog_joystick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        MainActivity.joystick_mode = !isChecked;
                                                        Log.e("joystick mode", String.valueOf(MainActivity.joystick_mode));
                                                    }
                                                }

        );
    }

    @Override
    public String setDroneInfo() {
        return "수정된 값";
    }
}
