package com.example.dron.Connecting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.R;

import java.sql.BatchUpdateException;

public class Fragment_Trimming extends SubMenuFragment {

    RadioButton[] radio_trim;
    TextView tv_roll, tv_pitch;
    Button[] btn_hand;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trimming, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

        radio_trim = new RadioButton[3];
        btn_hand = new  Button[4];

        radio_trim[0] = (RadioButton) v.findViewById(R.id.radio_value1);
        radio_trim[1] = (RadioButton) v.findViewById(R.id.radio_value2);
        radio_trim[2] = (RadioButton) v.findViewById(R.id.radio_value3);

        btn_hand[0] = (Button) v.findViewById(R.id.btn_handUP);
        btn_hand[1] = (Button) v.findViewById(R.id.btn_handDW);
        btn_hand[2] = (Button) v.findViewById(R.id.btn_handLF);
        btn_hand[3] = (Button) v.findViewById(R.id.btn_handRG);

        tv_roll = (TextView) v.findViewById(R.id.tv_roll);
        tv_pitch = (TextView) v.findViewById(R.id.tv_pitch);

        v.findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }



    @Override
    public String setDroneInfo() {

        return "수정된 값";

    }
}
