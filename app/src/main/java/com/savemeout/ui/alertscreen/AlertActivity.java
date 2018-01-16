package com.savemeout.ui.alertscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.savemeout.R;

public class AlertActivity extends AppCompatActivity {


    //private final Context context;

    /*public AlertActivity(@NonNull Context context) {
        super(context);
        this.context = context;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        setContentView(R.layout.activity_dialog);
       /* DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        getWindow().setLayout(width, height);*/
       
    }


}
