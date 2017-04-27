package com.yuki312.androiddaggersample;

import dagger.Module;
import dagger.Provides;

/**
 * Created by YukiMatsumura on 2017/04/27.
 */
@Module(subcomponents = { MainComponent.class })
public class AppModule {

  @Provides public String provideStr() {
    return "hoge";
  }
}
