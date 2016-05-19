package com.example.dron.Setting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dron.R;

public class SettingActivity extends AppCompatActivity implements Fragment_SettingMenu.OnMenuSelectedListener{

    String title = "Setting";

    TextView tv_title;
    ImageButton btn_back;
    ImageButton btn_save;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        manager = getFragmentManager();

        initComponent();
    }

    //애니메이션 효과 없애기
    @Override
    protected void onResume(){
        this.overridePendingTransition(0, 0);
        super.onResume();
    }

    /*initialize btn name and event*/
    private void initComponent() {

        setMenuFragment();

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_save = (ImageButton) findViewById(R.id.btn_save);

        tv_title.setText(title);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*save the setting*/
            }
        });
    }

    private void setMenuFragment() {

        String[] menuTitle = {"Control Sensitivity", "Camera Setting",
                "Alert & Warning", "User Settings", "JoyStick Setting"};

        int[] iconRes = {R.drawable.set_icon_equalizer, R.drawable.set_icon_camera,
                R.drawable.set_icon_alert, R.drawable.set_icon_user, R.drawable.set_icon_joystick};

        Fragment fragment = new Fragment_SettingMenu();
        Bundle   bundle   = new Bundle();
        bundle.putStringArray("menuTitle", menuTitle);
        bundle.putIntArray("iconRes", iconRes);

        fragment.setArguments(bundle);

        addFragment(fragment, null);
    }

    private void onClickBack(){

        int fragment_count = getFragmentManager().getBackStackEntryCount();

        if(fragment_count != 0) {
            manager.popBackStack();
            tv_title.setText(title);
        }
        else finish();
    }

    public void OnMenuSelected(int fragment_idx, String fragment_title) {

        int fragment_count = getFragmentManager().getBackStackEntryCount();
        if(fragment_count != 0) return;

        Fragment fragment = null;
        Bundle   bundle   = new Bundle();

        switch (fragment_idx){

            case 0:
                fragment = new Fragment_ControlSensit();

                int drone_sensitive = 1;
                bundle.putInt("initialValue", drone_sensitive);
                break;

            case 1:
                Toast toast = Toast.makeText(this, "추후 구현될 기능입니다.", Toast.LENGTH_SHORT);
                toast.show();
                return;
                // fragment = new Fragment_CameraSetting(); break;


            case 2:
                fragment = new Fragment_AlertWarning();

                boolean[] drone_alertvalue = {true, true, true, true};
                int       drone_tension    = 5;

                bundle.putBooleanArray("initialValue", drone_alertvalue);
                bundle.putInt("initialProgress", drone_tension);
                break;

            case 3:
                toast = Toast.makeText(this, "추후 구현될 기능입니다.", Toast.LENGTH_SHORT);
                toast.show();
                return;
                //fragment = new Fragment_UserSetting(); break;

            case 4:
                fragment = new Fragment_JoystickSetting();
                break;

            default:
                break;
        }

        fragment.setArguments(bundle);
        addFragment(fragment, fragment_title);
    }



    private void addFragment(Fragment fragment, String fragment_title){

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frag_container, fragment);

        if (fragment_title != null ) {
            transaction.addToBackStack(null);
            tv_title.setText(fragment_title);
        }

        transaction.commit();
    }

}

