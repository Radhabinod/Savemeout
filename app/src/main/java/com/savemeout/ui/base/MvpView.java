package com.savemeout.ui.base;

/**
 * Created by binod on 8/1/18.
 */

public interface MvpView {
void showProgress(String text);
void hideProgress();
void showToast(String msg);
}
