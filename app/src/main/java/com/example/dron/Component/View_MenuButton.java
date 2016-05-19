package com.example.dron.Component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dron.R;

/**
 * Created by chorong on 16. 3. 22..
 */
public class View_MenuButton extends LinearLayout {

    LinearLayout bg;
    ImageView    icon;
    TextView     title;

    public View_MenuButton(Context context) {
        super(context);
        initView();
    }

    public View_MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public View_MenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        getAttrs(attrs, defStyleAttr);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuButton);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuButton, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {

        int icon_resID = typedArray.getResourceId(R.styleable.MenuButton_m_icon, R.drawable.connect_icon_bluetooth);
        icon.setImageResource(icon_resID);

        String text_string = typedArray.getString(R.styleable.MenuButton_m_title);
        title.setText(text_string);

        typedArray.recycle();
    }

    private void initView(){
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_menubutton, this, false);
        addView(v);

        bg    = (LinearLayout) findViewById(R.id.bg);
        icon  = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.titile);
    }

    public void setIcon(int icon_resId){
        this.icon.setImageResource(icon_resId);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle(){ return this.title.getText().toString();  }
    public void setTitle_resId(int title_resId){
        this.title.setText(title_resId);
    }

    public void setTextSize(float size){
        title.setTextSize(size);
    }
    private void setIconImg(){

    }

    public void recycleImgres(){

        Drawable dw = icon.getDrawable();
        if(dw instanceof BitmapDrawable){
            Bitmap b = ((BitmapDrawable)dw).getBitmap();
            b.recycle();
            b = null;
        }

        dw.setCallback(null);
    }

}
