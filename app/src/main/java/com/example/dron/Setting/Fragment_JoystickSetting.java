package com.example.dron.Setting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

public class Fragment_JoystickSetting extends SubMenuFragment {

    TextView tv_jmode;
    ToggleButton[] tog_jmode;
    ImageView[] img_jmode;

    int[] joystick = {
            R.drawable.controller_left,
            R.drawable.controller_right,
            R.drawable.controller_right
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_joysticksetting, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

        int mode = MainActivity.drone.getJoystickMode();

        tog_jmode = new ToggleButton[3];
        img_jmode = new ImageView[2];

        tv_jmode = (TextView) v.findViewById(R.id.tv_jmode);
        tog_jmode[0] = (ToggleButton) v.findViewById(R.id.tog_jmode1);
        tog_jmode[1] = (ToggleButton) v.findViewById(R.id.tog_jmode2);
        tog_jmode[2] = (ToggleButton) v.findViewById(R.id.tog_jmode3);
        img_jmode[0] = (ImageView) v.findViewById(R.id.img_jmodeL);
        img_jmode[1] = (ImageView) v.findViewById(R.id.img_jmodeR);

        for(int i=0; i<3; i++) {
            tog_jmode[i].setOnClickListener(mClickListner);
        }

        mClickListner.onClick(tog_jmode[mode]);

    }

    ToggleButton.OnClickListener mClickListner = new View.OnClickListener(){
        public void onClick(View v){

            switch (v.getId()){
                case R.id.tog_jmode1 :
                    setToggMode(0, 1, 2);
                    img_jmode[0].setImageResource(joystick[0]);
                    img_jmode[1].setImageResource(joystick[0]);
                    break;

                case R.id.tog_jmode2 :
                    setToggMode(1,0,2);
                    img_jmode[0].setImageResource(joystick[0]);
                    img_jmode[1].setImageResource(joystick[1]);
                    break;


                case R.id.tog_jmode3 :
                    setToggMode(2, 0, 1);
                    img_jmode[0].setImageResource(joystick[1]);
                    img_jmode[1].setImageResource(joystick[0]);

                    break;
            }

        }

    };

    void setToggMode(int _s, int _m, int _n){

        MainActivity.drone.setJoystickMode(_s);

        tog_jmode[_s].setChecked(true);
        tog_jmode[_m].setChecked(false);
        tog_jmode[_n].setChecked(false);

        tv_jmode.setText("모드 " + (_s+1));
    }


    @Override
    public String setDroneInfo() {
        return "수정된 값";
    }
}
