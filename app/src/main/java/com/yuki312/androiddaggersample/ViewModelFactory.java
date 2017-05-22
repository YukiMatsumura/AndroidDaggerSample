package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.v4.util.ArrayMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

  private final ArrayMap<Class, Callable<? extends ViewModel>> creators;

  @Inject
  public ViewModelFactory(final ViewModelComponent viewModelComponent) {
    creators = new ArrayMap<>();
    // we cannot inject view models directly because they won't be bound to the owner's
    // view model scope.
    creators.put(MyViewModel.class,
        // TODO: replace lambda code.
        new Callable<ViewModel>() {
          @Override public ViewModel call() throws Exception {
            return viewModelComponent.myViewModel();
          }
        });
  }

  @Override public <T extends ViewModel> T create(Class<T> modelClass) {
    Callable<? extends ViewModel> creator = creators.get(modelClass);
    if (creator == null) {
      for (Map.Entry<Class, Callable<? extends ViewModel>> entry : creators.entrySet()) {
        if (modelClass.isAssignableFrom(entry.getKey())) {
          creator = entry.getValue();
          break;
        }
      }
    }
    if (creator == null) {
      throw new IllegalArgumentException("unknown model class " + modelClass);
    }
    try {
      return (T) creator.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
