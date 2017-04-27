package com.yuki312.androiddaggersample;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by YukiMatsumura on 2017/04/27.
 */
@Subcomponent
public interface MainComponent extends AndroidInjector<MainActivity> {

  @Subcomponent.Builder
  abstract class Builder extends AndroidInjector.Builder<MainActivity> {
  }
}
