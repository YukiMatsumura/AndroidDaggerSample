package com.yuki312.androiddaggersample;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseObject {

  @Inject
  public DatabaseObject() {
    android.util.Log.d("TEST", "Init DatabaseObject hash=" + hashCode());
  }
}
