package com.savemeout.di.component;

import android.content.ContentResolver;
import android.content.Context;


import com.savemeout.MyApp;
import com.savemeout.di.annotation.ApplicationContext;
import com.savemeout.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by binod on 8/1/18.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MyApp app);

    @ApplicationContext
    Context context();

    ContentResolver getResover();

}
