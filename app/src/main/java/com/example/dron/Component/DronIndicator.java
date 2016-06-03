package com.example.dron.Component;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dron.MainActivity;
import com.example.dron.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DronIndicator {

    TextView speedText, dateText, directionText;
    Activity mActivity;

    public DronIndicator(Activity activity){

        this.mActivity = activity;
        initIndicatorComponent();
    }

    public void initIndicatorComponent(){
        speedText     = (TextView) mActivity.findViewById(R.id.speedText);
        dateText      = (TextView) mActivity.findViewById(R.id.dateText);
        directionText = (TextView) mActivity.findViewById(R.id.directionText);

    }

    public void setDronIndicator(String _speed, String _battery, String _direction){

        speedText.setText(_speed);
        directionText.setText(_direction);
    }

    public void setCurrentTime(){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strResult = sdfNow.format(new Date(System.currentTimeMillis()));
        Log.d("timt",strResult);
        dateText.setText(strResult);
    }

}
