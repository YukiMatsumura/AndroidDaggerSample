package com.yuki312.androiddaggersample;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = { MainComponent.class })
public class AppModule {

  @Provides public String provideStr() {
    return "hoge";
  }
}
