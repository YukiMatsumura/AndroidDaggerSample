package com.yuki312.androiddaggersample;

import dagger.Subcomponent;

@Subcomponent
public interface ViewModelComponent {

  @Subcomponent.Builder
  interface Builder {
    ViewModelComponent build();
  }

  MyViewModel myViewModel();
}
