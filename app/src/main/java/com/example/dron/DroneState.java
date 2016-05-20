package com.example.dron;

/**
 * Created by chorong on 2016. 5. 18..
 */
public class DroneState {

    private int   controlSensitivity;
    private AlertState alertState;
    private int joystickMode;

    public DroneState(){

        alertState = new AlertState(false, false, false, false,5);
        controlSensitivity = 1;

        joystickMode = 1;
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

        public AlertState(boolean _volt, boolean _marf, boolean _crash, boolean _area, int _limit){

            mode = new boolean[4];

            mode[0] = _volt;
            mode[1] = _marf;
            mode[2] = _crash;
            mode[3] = _area;

            limit = _limit;
        }

    }

}
