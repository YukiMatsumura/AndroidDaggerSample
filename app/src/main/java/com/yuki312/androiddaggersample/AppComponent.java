package com.yuki312.androiddaggersample;

import android.app.Application;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

@Singleton
@Component(modules = { AndroidSupportInjectionModule.class, AppModule.class, MainModule.class })
public interface AppComponent extends AndroidInjector<App> {

  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<App> {
  }
}
