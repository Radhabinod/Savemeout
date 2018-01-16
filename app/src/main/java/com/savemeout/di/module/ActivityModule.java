package com.savemeout.di.module;

import android.app.Activity;
import android.content.Context;


import com.savemeout.di.annotation.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by binod on 8/1/18.
 */
@Module
public class ActivityModule {
    Activity context;

    public ActivityModule(Activity context) {
        this.context = context;
    }

    @Provides
    @ActivityContext
    Context getContext() {
        return context;
    }



}
