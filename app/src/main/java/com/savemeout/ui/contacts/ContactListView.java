package com.savemeout.ui.contacts;

import com.savemeout.ui.base.MvpView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by binod on 16/1/18.
 */

public interface ContactListView extends MvpView {

    void setContact(ArrayList<HashMap<String, String>> contacts);
}
