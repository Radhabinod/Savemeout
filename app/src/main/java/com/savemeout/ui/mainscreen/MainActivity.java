package com.savemeout.ui.mainscreen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.savemeout.R;
import com.savemeout.ui.base.BaseActivity;
import com.savemeout.ui.contacts.ContactListActivity;
import com.savemeout.ui.voicetotext.VoiceRecognitionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.btnContact)
    Button btnContact;
    @BindView(R.id.btnDialog)
    Button btnDialog;
    @BindView(R.id.btnVoiceRecog)
    Button btnVoiceRecog;
    String[] permissions = {android.Manifest.permission.SEND_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnContact)
    public void goToContact() {
        startActivity(new Intent(this, ContactListActivity.class));
    }

    @OnClick(R.id.btnDialog)
    public void goToFullScreeDialog() {
        // startActivity(new Intent(this, AlertActivity.class));
        if (hasPermissions(permissions)) {
            sendSms();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
    }

    @OnClick(R.id.btnVoiceRecog)
    public void voiceRecog() {
        startActivity(new Intent(this, VoiceRecognitionActivity.class));
    }

    private void sendSms() {


        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
        String phoneNumber = "7814695551";
        String smsBody = "Message from the API";

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (hasPermissions(permissions)) {
                Log.e("has", "true");
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                sendSms();
            } else {
                finish();
            }
        }
    }

}
