package com.yuki312.androiddaggersample;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.yuki312.androiddaggersample.databinding.ActivityMainBinding;
import dagger.android.AndroidInjection;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

  @Inject ViewModelProvider.Factory vmFactory;
  MyViewModel vm;

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);

    android.util.Log.d("TEST", "onCreate Activity hash=" + hashCode());

    vm = ViewModelProviders.of(this, vmFactory).get(MyViewModel.class);

    ActivityMainBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_main);
    bind.button.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
  }

  @Override protected void onResume() {
    super.onResume();
    android.util.Log.d("TEST", "onResume Activity hash=" + hashCode());
    android.util.Log.d("TEST",
        "  >>> DUMP Activity hash=" + hashCode() + ", ViewModel hash=" + vm.hashCode());
  }
}
