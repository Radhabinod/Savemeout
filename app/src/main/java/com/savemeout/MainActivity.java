package com.savemeout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.savemeout.alertscreen.AlertActivity;
import com.savemeout.contacts.ContactList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnContact)
    Button btnContact;
    @BindView(R.id.btnDialog)
    Button btnDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnContact)
    public void goToContact() {
        startActivity(new Intent(this, ContactList.class));
    }
    @OnClick(R.id.btnDialog)
    public void goToFullScreeDialog()
    {
        startActivity(new Intent(this, AlertActivity.class));
    }
}
