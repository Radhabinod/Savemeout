package com.savemeout.di.component;


import com.savemeout.di.annotation.PerActivity;
import com.savemeout.di.module.ActivityModule;
import com.savemeout.ui.contacts.ContactListActivity;

import dagger.Component;

/**
 * Created by binod on 8/1/18.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

void inject(ContactListActivity c);

}
