package com.yuki312.androiddaggersample;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by YukiMatsumura on 2017/04/27.
 */
public class App extends DaggerApplication {

  @Override protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }
}
