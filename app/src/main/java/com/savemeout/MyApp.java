package com.savemeout;

import android.app.Application;

import com.savemeout.di.component.AppComponent;
import com.savemeout.di.component.DaggerAppComponent;
import com.savemeout.di.module.AppModule;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by binod on 3/1/18.
 */

public class MyApp extends Application {

    AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LATO-REGULAR.TTF")
                .build());
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        appComponent.inject(this);
    }
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
