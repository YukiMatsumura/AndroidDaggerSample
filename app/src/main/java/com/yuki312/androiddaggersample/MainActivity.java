package com.yuki312.androiddaggersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import dagger.android.AndroidInjection;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
}
