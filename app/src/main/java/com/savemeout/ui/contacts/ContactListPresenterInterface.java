package com.savemeout.ui.contacts;

import com.savemeout.ui.base.MvpPresenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by binod on 16/1/18.
 */

public interface ContactListPresenterInterface<V extends ContactListView> extends MvpPresenter<V> {
    public ArrayList<HashMap<String, String>> getContacts();
    public void loadContact();
    public boolean addContact(String DisplayName, String MobileNumber, String emailID);

}
