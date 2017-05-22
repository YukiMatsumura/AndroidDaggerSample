package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import dagger.android.AndroidInjection;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

  @Inject ViewModelProvider.Factory vmFactory;

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);

    super.onCreate(savedInstanceState);
    MyViewModel vm = ViewModelProviders.of(this, vmFactory).get(MyViewModel.class);
    setContentView(R.layout.activity_main);

    android.util.Log.d("TEST", "ViewModel instance hash=" + vm.hashCode());
  }
}
