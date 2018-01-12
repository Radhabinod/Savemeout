package com.savemeout.voicetotext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savemeout.R;
import com.savemeout.utils.Constants;
import com.services.CommonVoiceListenerService;

import pl.droidsonroids.gif.GifImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class WakeUp extends AppCompatActivity {

    GifImageView gif;
    private BroadcastReceiver receiver;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    TextView tvCaptionText, tvResultText;
    ImageView ivOpenMic;
    String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
        tvCaptionText = (TextView) findViewById(R.id.caption_text);
        tvResultText = (TextView) findViewById(R.id.result_text);
        ivOpenMic = (ImageView) findViewById(R.id.ivOpenMic);
        if (CommonVoiceListenerService.IS_SERVICE_RUNNING) {
            tvCaptionText.setText(getString(R.string.kws_caption));
        } else {
            tvCaptionText.setText("Preparing the recognizer");
        }
        gif = (GifImageView) findViewById(R.id.gif);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bd = intent.getExtras();
                if (bd != null) {
                    String type = bd.getString("type");
                    Log.e("typeeeeee", type);
                    if (type.equals(Constants.ps_initialization_complete)) {
                        tvCaptionText.setText(getString(R.string.kws_caption));
                        tvResultText.setText("");
                        ivOpenMic.setVisibility(View.VISIBLE);
                    } else if (type.equals(Constants.listening_started_ui)) {
                        ivOpenMic.setVisibility(View.INVISIBLE);
                        gif.setVisibility(View.VISIBLE);
                        tvResultText.setText("");
                    } else if (type.equals(Constants.listening_end)) {
                        ivOpenMic.setVisibility(View.VISIBLE);
                        gif.setVisibility(View.INVISIBLE);
                        String txt = bd.getString("text");
                        tvResultText.setText(txt);
                    }

                }

            }
        };
        registerReceiver(receiver, new IntentFilter(Constants.intentfilterfromservice));
        // Check if user has given permission to record audio
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }

        ivOpenMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Constants.intentfilterfromactivity);
                intent.putExtra("type", Constants.start_listener);
                sendBroadcast(intent);
                gif.setVisibility(View.VISIBLE);
                ivOpenMic.setVisibility(View.INVISIBLE);

            }
        });

        if (!CommonVoiceListenerService.IS_SERVICE_RUNNING) {
            startVoiceListenerService();
        } else {
            ivOpenMic.setVisibility(View.VISIBLE);
            tvResultText.setText("");
        }
    }

    boolean hasPermissions(String[] perms) {
        for (int i = 0; i < perms.length; i++) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), perms[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    void startVoiceListenerService() {
        Intent it = new Intent(WakeUp.this, CommonVoiceListenerService.class);
        startService(it);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            /*for (int i = 0; i < permissions.length; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Recognizer initialization is a time-consuming and it involves IO,
                    // so we execute it in async task

                }
            }*/
            if (hasPermissions(permissions)) {
                Log.e("has", "true");
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                startVoiceListenerService();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
