package com.savemeout.ui.base;

/**
 * Created by binod on 8/1/18.
 */

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
