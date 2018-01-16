package com.savemeout.ui.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.savemeout.MyApp;
import com.savemeout.di.component.ActivityComponent;
import com.savemeout.di.component.DaggerActivityComponent;
import com.savemeout.di.module.ActivityModule;
import com.savemeout.utils.views.DialogUtils;


public class BaseActivity extends AppCompatActivity implements MvpView {

    ActivityComponent activityComponent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null)
            activityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).appComponent(((MyApp) getApplication()).getAppComponent()).build();
        return activityComponent;
    }

    @Override
    public void showProgress(String text) {
        progressDialog = DialogUtils.showLoadingDialog(this,text);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermissions(String[] perms) {
        for (int i = 0; i < perms.length; i++) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), perms[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
