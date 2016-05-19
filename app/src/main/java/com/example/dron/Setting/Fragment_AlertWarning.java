package com.example.dron.Setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.MainActivity;
import com.example.dron.R;

public class Fragment_AlertWarning extends SubMenuFragment {

    ToggleButton[] alertMode;
    TextView tv_limit;
    SeekBar sbar_limit;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alertwarning, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

        boolean[] _mode = MainActivity.drone.getAlertState();
        int _voltLimit = MainActivity.drone.getVoltLimit();

        alertMode = new ToggleButton[4];

        alertMode[0] = (ToggleButton) v.findViewById(R.id.tog_voltage_limit);
        alertMode[1] = (ToggleButton) v.findViewById(R.id.tog_malfunction_warning);
        alertMode[2] = (ToggleButton) v.findViewById(R.id.tog_crash_warning);
        alertMode[3] = (ToggleButton) v.findViewById(R.id.tog_area_warning);

        tv_limit = (TextView) v.findViewById(R.id.tv_limit);
        sbar_limit = (SeekBar) v.findViewById(R.id.sbar_limit);

        for (int i=0; i<4; i++){
            alertMode[i].setChecked(_mode[i]);
            alertMode[i].setOnClickListener(mClickListner);
        }

        sbar_limit.setProgress(_voltLimit / 5);
        tv_limit.setText(_voltLimit + "%");
        setSeekbarState(_mode[0]);

        sbar_limit.setOnSeekBarChangeListener(mChangeListener);
    }

    SeekBar.OnSeekBarChangeListener mChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

            int _limit = progress * 5;
            MainActivity.drone.setVoltLimit(_limit);
            tv_limit.setText(_limit+"%");
        }
    };

    ToggleButton.OnClickListener mClickListner = new View.OnClickListener() {
        public void onClick(View v) {
            boolean _state = ((ToggleButton)v).isChecked();

            switch (v.getId()) {
                case R.id.tog_voltage_limit:
                    MainActivity.drone.setAlertState(0, _state);
                    setSeekbarState(_state);
                    break;

                case R.id.tog_malfunction_warning:
                    MainActivity.drone.setAlertState(1, _state);
                    break;

                case R.id.tog_crash_warning:
                    MainActivity.drone.setAlertState(2, _state);
                    break;

                case R.id.tog_area_warning:
                    MainActivity.drone.setAlertState(3, _state);
                    break;

            }
        }
    };

    private void setSeekbarState(boolean _state){
        sbar_limit.setEnabled(_state);
        if(_state){
            tv_limit.setVisibility(View.VISIBLE);
        }
        else {
            tv_limit.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public String setDroneInfo() {
        return "수정된 값";
    }
}
