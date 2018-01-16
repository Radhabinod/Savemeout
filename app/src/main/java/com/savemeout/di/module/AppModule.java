package com.savemeout.di.module;

import android.content.ContentResolver;
import android.content.Context;

import com.savemeout.di.annotation.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by binod on 8/1/18.
 */
@Module
public class AppModule {
    Context appContext;

    public AppModule(Context context) {
        appContext = context;
    }

    @Provides
    @ApplicationContext
    Context getAppContext() {
        return appContext;
    }

    @Provides
    ContentResolver getContentResolver(@ApplicationContext Context context){
        return context.getContentResolver();
    }



}
