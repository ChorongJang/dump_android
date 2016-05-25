package com.example.dron;

import android.content.SharedPreferences;
import android.util.Log;

public class DroneState {
    SharedPreferences mPref;
    String[] stateName = {"JoystickMode", "ControlSensitivity",
            "_aVolt", "_aMarf", "_aCrash", "_aArea", "_aLimit"};

    private int joystickMode;
    private int controlSensitivity;
    private AlertState alertState;


    public DroneState( SharedPreferences _mPref){
        mPref = _mPref;
        boolean[] aMode = new boolean[4];
        int aLimit;

        joystickMode = _mPref.getInt(stateName[0], 1);
        controlSensitivity = _mPref.getInt(stateName[1], 1);
        aMode[0] = _mPref.getBoolean(stateName[2], false);
        aMode[1] = _mPref.getBoolean(stateName[3], false);
        aMode[2] = _mPref.getBoolean(stateName[4], false);
        aMode[3] = _mPref.getBoolean(stateName[5], false);
        aLimit = _mPref.getInt(stateName[6], 10);

        alertState = new AlertState(aMode, aLimit);

        Log.d("sf_test", joystickMode+" - "+controlSensitivity + " - "+aMode.toString()+" - " +aLimit);
    }

    public void saveDroneState(){

        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(stateName[0], joystickMode);
        editor.putInt(stateName[1], controlSensitivity);

        editor.putBoolean(stateName[2], alertState.mode[0]);
        editor.putBoolean(stateName[3], alertState.mode[1]);
        editor.putBoolean(stateName[4], alertState.mode[2]);
        editor.putBoolean(stateName[5], alertState.mode[3]);
        editor.putInt(stateName[6], alertState.limit);
    }
    public void setControlSensitivity(int _sensitivity){
        this.controlSensitivity = _sensitivity;
    }

    public void setJoystickMode(int _mode){
        this.joystickMode = _mode;
    }

    public void setAlertState(int _value,  boolean _state){
        alertState.mode[_value] = _state;
    }

    public void setVoltLimit(int _limit){
        alertState.limit = _limit;
    }


    public int getControlSensitivity(){
        return controlSensitivity;
    }

    public int getJoystickMode(){
       return this.joystickMode;
    }

    public boolean[] getAlertState(){
        return alertState.mode;
    }

    public int getVoltLimit(){
        return alertState.limit;
    }


    public class AlertState{

        public boolean[] mode;
        public int limit;

        public AlertState(boolean[] _mode, int _limit){

            mode = _mode;
            limit = _limit;
        }

    }

}
