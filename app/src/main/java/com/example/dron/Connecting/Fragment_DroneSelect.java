package com.example.dron.Connecting;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.Component.View_MenuButton;
import com.example.dron.R;

/**
 * Created by chorong on 16. 4. 8..
 */
public class Fragment_DroneSelect extends SubMenuFragment {

    LinearLayout layout_droneSelect;

    String[] droneName = {"Educopter Nano", "Educopter Nano\nHexa", "Educopter Fortis", "Educopter Fortis\nHexa"};
    int[]    imgRes    = {R.drawable.connect_icon_dron,R.drawable.connect_icon_dron,
                            R.drawable.connect_icon_dron,R.drawable.connect_icon_dron};

    String selectedDrone;/* 드론 객체 만들기 */

    View_MenuButton[] btn_drone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_droneselect, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v){

        btn_drone = new View_MenuButton[4];
        layout_droneSelect = (LinearLayout) v.findViewById (R.id.layout_droneselect);
        int i=0;

        for(; i<droneName.length; i++){

            btn_drone[i] = new View_MenuButton(getActivity());

            LinearLayout.LayoutParams position = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    450, 1);
            btn_drone[i].setIcon(imgRes[i]);
            btn_drone[i].setTitle(droneName[i]);

            btn_drone[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View_MenuButton m = (View_MenuButton) v;
                    selectedDrone = m.getTitle();
                }
            });
            layout_droneSelect.addView(btn_drone[i], position);
        }
    }

    @Override
    public String setDroneInfo() {

        //Drone 변경
        return "true";

    }
}
