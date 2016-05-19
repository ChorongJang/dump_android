package com.example.dron.Connecting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dron.Component.View_MenuButton;
import com.example.dron.R;

public class Fragment_ConnectingMenu extends Fragment {

    String[] menuTitle;
    int[] iconRes;
    OnMenuSelectedListener mListener;
    View_MenuButton[] btn_menu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_connectmenu, container, false);

        menuTitle = getArguments().getStringArray("menuTitle");
        iconRes   = getArguments().getIntArray("iconRes");

        try {
            mListener = (OnMenuSelectedListener) this.getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(this.getActivity().toString());
        }

        initConnectingMainComp(v);
        return v;
    }


    @Override
    public void onDestroy() {
        recycleView();
        super.onDestroy();
    }

    public interface OnMenuSelectedListener{
        void OnMenuSelected(int fragment_idx, String fragment_title);
    }

    private void initConnectingMainComp(View v) {

        btn_menu = new View_MenuButton[4];

        btn_menu[0] = (View_MenuButton) v.findViewById(R.id.btn_menu0);
        btn_menu[1] = (View_MenuButton) v.findViewById(R.id.btn_menu1);
        btn_menu[2] = (View_MenuButton) v.findViewById(R.id.btn_menu2);
        btn_menu[3] = (View_MenuButton) v.findViewById(R.id.btn_menu3);

        initButtonEvent();
    }

    private void initButtonEvent() {

        for (int i = 0; i < 4; i++) {
            btn_menu[i].setTitle(menuTitle[i]);
            btn_menu[i].setIcon(iconRes[i]);
        }

        btn_menu[0].setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v) {mListener.OnMenuSelected(0, menuTitle[0]);}
        });
        btn_menu[1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mListener.OnMenuSelected(1, menuTitle[1]);

            }
        });
        btn_menu[2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mListener.OnMenuSelected(2, menuTitle[2]);

            }
        });
        btn_menu[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnMenuSelected(3, menuTitle[3]);
            }
        });
    }

    private void recycleView()
    {
        for(int i=0; i<4; i++){
            btn_menu[i].recycleImgres();
        }
    }

}
