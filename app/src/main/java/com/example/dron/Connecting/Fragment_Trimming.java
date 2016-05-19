package com.example.dron.Connecting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dron.Component.SubMenuFragment;
import com.example.dron.R;

public class Fragment_Trimming extends SubMenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trimming, container, false);
    }

    @Override
    public String setDroneInfo() {

        return "수정된 값";

    }
}
