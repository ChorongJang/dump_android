package com.example.dron.Setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.R;

public class    Fragment_CameraSetting extends SubMenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camerasetting, container, false);

        initFragmentComponent(v);
        return v;
    }

    public void initFragmentComponent(View v) {

    }

    @Override
    public String setDroneInfo() {
        return "수정된 값";
    }

}
