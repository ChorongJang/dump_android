package com.example.dron.Component;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.dron.MainActivity;
import com.example.dron.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DronIndicator {

    TextView speedText, dateText, directionText;
    View     view_battery1, view_battery2, view_battery3;
    Activity mActivity;

    public DronIndicator(Activity activity){

        this.mActivity = activity;
        initIndicatorComponent();
    }

    public void initIndicatorComponent(){
        speedText     = (TextView) mActivity.findViewById(R.id.speedText);
        dateText      = (TextView) mActivity.findViewById(R.id.dateText);
        directionText = (TextView) mActivity.findViewById(R.id.directionText);

        view_battery1 = mActivity.findViewById(R.id.view_battery1);
        view_battery2 = mActivity.findViewById(R.id.view_battery2);
        view_battery3 = mActivity.findViewById(R.id.view_battery3);
    }

    public void setDronIndicator(String _speed, String _battery, String _direction){

        speedText.setText(_speed);
        directionText.setText(_direction);

        drawBatteryState(Integer.parseInt(_battery));
    }

    public void setCurrentTime(){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strResult = sdfNow.format(new Date(System.currentTimeMillis()));
        dateText.setText(strResult);
    }

    private void drawBatteryState(int _battery){

        switch (_battery){
            case 0:
                view_battery1.setVisibility(View.INVISIBLE);
                view_battery2.setVisibility(View.INVISIBLE);
                view_battery3.setVisibility(View.INVISIBLE);
                break;

            case 1:
                view_battery1.setVisibility(View.VISIBLE);
                view_battery2.setVisibility(View.INVISIBLE);
                view_battery3.setVisibility(View.INVISIBLE);
                break;

            case 2:
                view_battery1.setVisibility(View.VISIBLE);
                view_battery2.setVisibility(View.VISIBLE);
                view_battery3.setVisibility(View.INVISIBLE);
                break;

            case 3:
                view_battery1.setVisibility(View.VISIBLE);
                view_battery2.setVisibility(View.VISIBLE);
                view_battery3.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }
}
