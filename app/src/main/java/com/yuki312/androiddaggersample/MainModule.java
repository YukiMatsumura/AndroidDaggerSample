package com.yuki312.androiddaggersample;

import android.app.Activity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by YukiMatsumura on 2017/04/27.
 */
@Module
public abstract class MainModule {

  @Binds @IntoMap @ActivityKey(MainActivity.class)
  abstract AndroidInjector.Factory<? extends Activity> bindInjectorFactory(
      MainComponent.Builder builder);
}
