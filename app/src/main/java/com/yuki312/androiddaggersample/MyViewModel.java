package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModel;
import javax.inject.Inject;

public class MyViewModel extends ViewModel {

  @Inject
  public MyViewModel(DatabaseObject db) {
    android.util.Log.d("TEST", "New Database hash=" + db.hashCode());
  }
}
