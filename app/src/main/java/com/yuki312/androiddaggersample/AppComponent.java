package com.yuki312.androiddaggersample;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by YukiMatsumura on 2017/04/27.
 */
@Component(modules = { AndroidSupportInjectionModule.class, AppModule.class, MainModule.class })
public interface AppComponent extends AndroidInjector<App> {

  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<App> {
  }
}
