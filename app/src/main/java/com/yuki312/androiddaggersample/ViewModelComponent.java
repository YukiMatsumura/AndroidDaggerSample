package com.yuki312.androiddaggersample;

import dagger.Subcomponent;

@ViewModelScope
@Subcomponent
public interface ViewModelComponent {

  @Subcomponent.Builder
  interface Builder {
    ViewModelComponent build();
  }

  MyViewModel myViewModel();
}
