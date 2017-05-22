package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(subcomponents = { ViewModelComponent.class, MainComponent.class })
public class AppModule {

  @Singleton @Provides public DatabaseObject provideDatabase() {
    return new DatabaseObject();
  }

  @Singleton @Provides public ViewModelProvider.Factory provideViewModelFactory(
      ViewModelComponent.Builder viewModelComponent) {
    return new ViewModelFactory(viewModelComponent.build());
  }
}
