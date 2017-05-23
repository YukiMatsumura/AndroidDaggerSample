package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModel;
import javax.inject.Inject;

public class MyViewModel extends ViewModel {

  @Inject
  public MyViewModel(DatabaseObject db) {
    android.util.Log.d("TEST",
        "Init MyViewModel hash=" + hashCode() + ", DatabaseObject hash=" + db.hashCode());
  }

  @Override protected void onCleared() {
    super.onCleared();
    android.util.Log.d("TEST", "onCleared MyViewModel hash=" + hashCode());
  }
}
