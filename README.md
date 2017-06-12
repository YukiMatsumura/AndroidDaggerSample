<img  src="https://github.com/YukiMatsumura/AndroidDaggerSample/blob/master/art/android_robot.png?raw=true" align="right" />

# DAGGER 2.11-rc1 SAMPLE

### 🗡 Dagger 2.11-rc1

Dagger2.10で[`dagger.android`モジュールがリリース](https://google.github.io/dagger/android)されました.  
本稿ではDagger2.10と2.11でリリースされた`dagger.android`モジュールの使い方について簡単に紹介したいと思います.  

本題へ入る前に, Dagger2.11では当然, 歴代のバージョンで追加されてきた機能を土台にしています.  
Daggerを触ったことがない人は [Android: Dagger2](http://yuki312.blogspot.jp/2016/03/android-dagger2.html) を.  
Subcomponentを使ったことがない人は[Android: Dagger2 - Subcomponent vs. dependencies](http://yuki312.blogspot.jp/2016/02/android-dagger2-subcomponent-vs.html)を.  
マルチバインディングを使ったことがない人は[Dagger2. MultibindingでComponentを綺麗に仕上げる](http://yuki312.blogspot.jp/2017/02/dagger2-multibindingcomponent.html)を一度読んでから本稿に戻ってくると理解しやすいと思います.  

Dagger（Dependency Injection）を最大限に活かせるのは, 依存オブジェクトをDagger自身が生成して, 依存性を満たすようにデザインすることでしょう. しかし, AndroidはActivityやFragmentといったOSが生成・管理するオブジェクトがあり, Daggerが全てを生成・管理することができません.  
そうした場合, 次のようにフィールドインジェクションを使って依存性を満たすことになります.  

```java
public class MainActivity extends Activity {
  @Inject Hoge hoge;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 必ず最初に実行すること!
    ((App) getContext().getApplicationContext())
        .getApplicationComponent()
        .newActivityComponentBuilder()
        .activity(this)
        .build()
        .inject(this);
    // ...
  }
}
```

これにはいくつかの問題があります.  

 1. まず, ActivityやFragment, Service, ContentProviderといったOS管理のクラスへインジェクションする数だけコピペコードが出来上がり, メンテナンス性を悪くします.
 2. そしてなにより, クラスが依存性を注入するオブジェクト（ComponentやModules）のことについてそれぞれのクラスが知っている必要があるため, Dependency Injectionのコア原則を破っています.  

今回紹介する`dagger.android`モジュールを導入すると, これらの問題を解決することができます.  

```
NOTE:
android.daggerモジュールはまだBetaバージョンのため今後変更される可能性があります.  
今でもクラス名がリネームされるなどしているため, 他でコードを参考にされる場合はdaggerのバージョンに注意する必要があります.  

本稿では現時点で最新のリリースバージョンDagger2.11-rc1を対象にしています.  
StableのDagger2.10からの変更点もありますので, Dagger2.10を使う場合は変更点にご注意ください.  

Dagger2.10 -> 2.11の変更点：
 - New API: @ContributesAndroidInjector simplifies the usage of dagger.android
 - All HasDispatching*Injectors are renamed to Has*Injector. They also return an AndroidInjector instead of a DispatchingAndroidInjector
 - Added DaggerApplication and DaggerContentProvider

リネーム情報はGitHubのリリースページに記載されています.  
https://github.com/google/dagger/releases
```


### 依存ライブラリの追加

まずはDagger2.11のライブラリを追加しないとはじまりません.  
build.gradleのdependenciesに次のライブラリを追加します.  

```
  // Core dependencies
  compile 'com.google.dagger:dagger:2.11-rc1'
  annotationProcessor 'com.google.dagger:dagger-compiler:2.11-rc1'

  // Android dependencies
  compile 'com.google.dagger:dagger-android:2.11-rc1'
  annotationProcessor 'com.google.dagger:dagger-android-processor:2.11-rc1'

  // Require if use android support libs.
  compile 'com.google.dagger:dagger-android-support:2.11-rc1'
```

`dagger-android-*`なモジュールがDaggerのAndroid拡張です.  
プロジェクトでサポートライブラリを使用している場合は`dagger-android-support`も必要です.    

余談ですが, 手元の環境ではfindbugsのdependencyでコンフリクトが起きたので, 合わせて解消しています.  

```
エラー：
Error:Conflict with dependency 'com.google.code.findbugs:jsr305' in project ':app'. Resolved versions for app (3.0.1) and test app (2.0.1) differ. See http://g.co/androidstudio/app-test-app-conflict for details.

解決： espresso-coreの依存モジュールからjsr305をexcludeしておく
  androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
    exclude group: 'com.android.support', module: 'support-annotations'
    exclude group: 'com.google.code.findbugs', module: 'jsr305'
  })
```

Daggerのライブラリを取得したらComponent, Moduleを作成していきましょう.  



### ActivityのComponent/Module作成

順を追って必要なオブジェクトを作って行きます. まずはMainActivityに紐づくMainComponentの定義から.

MainComponentはこのあと作るアプリケーションコンポーネントのサブコンポーネントとして定義するので`@Subcomponent`アノテーションをつけます.  
さらに, コンポーネントビルダー`@Subcomponent.Builder`を同じく宣言します.  

```
package com.yuki312.androiddaggersample;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface MainComponent extends AndroidInjector<MainActivity> {
  @Subcomponent.Builder
  abstract class Builder extends AndroidInjector.Builder<MainActivity> {
  }
}
```

MainComponentには`AndroidInjector`インタフェースを継承させます.   
`AndroidInjector`はAndroidのコアコンポーネント（Activity, Fragment, Service, BroadcastReceiver, ContentProvider）に依存性を注入するメソッド`inject(T)`を定義したインタフェースです.  

次にMainModuleを定義します.  

```
import android.app.Activity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainModule {

  @Binds @IntoMap @ActivityKey(MainActivity.class)
  abstract AndroidInjector.Factory<? extends Activity> bindInjectorFactory(
      MainComponent.Builder builder);
}
```

`@ActivityKey`でのMainActivity.class指定は, 後ほど説明する適切な`AndroidInjector.Builder`を選択するための型情報に必要なものです.  
Androidの各コアコンポーネント専用のInjectorを生成するファクトリをここで指定します. `AndroidInjector`については後ほど説明します.  

続いてアプリケーションクラス用のAppModule.

```java
package com.yuki312.androiddaggersample;

import dagger.Module;

@Module(subcomponents = { MainComponent.class })
public class AppModule {
}
```

そしてAppComponent.

```java
package com.yuki312.androiddaggersample;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = { AndroidSupportInjectionModule.class, AppModule.class, MainModule.class })
public interface AppComponent extends AndroidInjector<App> {

  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<App> {
  }
}
```

`modules={...}`にはインジェクションモジュールを含める必要があります.  
インジェクションモジュールには次の種類が用意されています.  

 - `AndroidInjectionModule.class`（サポートライブラリを使わない場合）
 - `AndroidSupportInjectionModule.class`（サポートライブラリを使う場合）

インジェクションモジュールには, AndroidのコアコンポーネントにinjectするComponent/SubComponentのファクトリクラスである`AndroidInjector.Factory`を値に持つMapがAndroidコアコンポーネント毎に定義されており, それぞれのインスタンスはマルチバイインディングの仕組みで構築されています.  

```
@Module
public abstract class AndroidInjectionModule {
 @Multibinds
  abstract Map<Class<? extends Activity>, AndroidInjector.Factory<? extends Activity>>
      activityInjectorFactories();

  @Multibinds
  abstract Map<Class<? extends Fragment>, AndroidInjector.Factory<? extends Fragment>>
      fragmentInjectorFactories();

  @Multibinds
  abstract Map<Class<? extends Service>, AndroidInjector.Factory<? extends Service>>
      serviceInjectorFactories();
 ...
```

`AndroidInjectionModule`, `AndroidSupportInjectionModule`が`AndroidInjector.Factory`の管理に必要であることがわかります.  
アプリケーション全体に渡るコアコンポーネントを管理するため, 基本的にはApplicationスコープのコンポーネントで管理することになります.  
AppComponentにはビルダー`AndroidInjector.Builder`も忘れずに定義しておきます.  



### DaggerApplication

次にApplicationクラスの定義です.  
Applicationクラスには各Androidコアコンポーネント用の`AndroidInjector`を定義する必要があります.  
`AndroidInjector`はActivityやFragmentといったコアコンポーネントに依存性を注入するためのインジェクター用のインタフェースです.  
コアコンポーネント用のインジェクターには次のものがあります.  

 - HasActivityInjector
 - HasFragmentInjector,
 - HasServiceInjector,
 - HasBroadcastReceiverInjector,
 - HasContentProviderInjector
 - HasSupportFragmentInjector（dagger-android-support）

それぞれのインタフェースには各コアコンポーネント専用のインジェクターを返すメソッドが定義されているわけですが, Applicationクラスでこれら全てのインジェクターを実装するのは面倒なので, Dagger2.11では`DaggerApplication`クラスが提供されました.  

 - dagger.android.DaggerApplication（サポートライブラリを使わない場合）
 - dagger.android.support.DaggerApplication（サポートライブラリを使う場合）

Dagger2.11-rc1ではサポートライブラリ対応/非対応でクラス名が同じなのでextendsする際には注意が必要です.  
また, DaggerApplicationはApplication用のインジェクターを返す`applicationInjector`をabstractメソッドとして定義してあるので, これをオーバーライドしておきます.  
これで, Applicationクラスへのフィールドインジェクションもサポートされます.  

```java
package com.yuki312.androiddaggersample;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class App extends DaggerApplication {

  @Override protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }
}
```


### 仕上げ

最後の仕上げにMainActivityでフィールドインジェクションを実装しましょう.  

```java
package com.yuki312.androiddaggersample;

...
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

  ...

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    ...
  }
}
```

`AndroidInjection.inject(this);`. たったこれだけです! 簡単ですね:)  
従来のComponentやModuleの指定が現れないのでDependency Injectionの原則にも忠実です.  



### おまけ

#### dagger-android-support は何者か

`dagger.android`の肝はAndroidコアコンポーネントへのインジェクションサポートです.  
今回登場した `HasSupportFragmentInjector`, `AndroidSupportInjectionModule`, `dagger.android.support.DaggerApplication`が主にサポートライブラリ向けのクラスになります.  
これらの中身を覗くと, `android.support.v4.app.Fragment`のためのバインディングマップであったり, インジェクターであったりの処理が定義されています.  
つまり, サポートライブラリのFragmentを使ったinjectionをサポートするためにこれらのライブラリが必要になってきます.  
サポートライブラリのFragmentを使わないのであれば必ずしも必要というわけではなさそうですね.  


#### コアコンポーネントのInjectorはどうやって選ばれる？

ActivityやFragmentといったコアコンポーネントのインジェクターは`AndroidInjectionModule`に定義された`AndroidInjector.Factory`から生成することができますが, これが設定されているマルチバインディングで構築されたMapからファクトリインスタンスを取り出す操作は`DispatchingAndroidInjector`が行なっています.  
`DispatchingAndroidInjector`はDaggerが生成するオブジェクトであるためアプリケーション側から直接触ることはないと思いますが, `dagger.android`の内部動作を把握するには押さえておく必要のあるクラスです.  


#### ContentProviderInjectorとApplicationInjector

Androidの仕組み上, アプリケーションプロセスがZygoteからforkされて開始される際, ContentProviderの初期化はApplicationの初期化より早いです.  
つまり, ActivityやBroadcastReceiver, Serviceなど他のコアコンポーネントと唯一異なってContentProviderのonCreate時にはまだApplicationクラスが初期化（onCreate）されていない可能性があります.  
DaggerApplicationクラスを覗くとこの辺りをどう解決しているのかをうかがい知ることができます.  

```java
  // injectIfNecessary is called here but not on the other *Injector() methods because it is the
  // only one that should be called (in AndroidInjection.inject(ContentProvider)) before
  // Application.onCreate()
  @Override
  public AndroidInjector<ContentProvider> contentProviderInjector() {
  ...


  /**
   * Lazily injects the {@link DaggerApplication}'s members. Injection cannot be performed in {@link
   * Application#onCreate()} since {@link android.content.ContentProvider}s' {@link
   * android.content.ContentProvider#onCreate() onCreate()} method will be called first and might
   * need injected members on the application. Injection is not performed in the the constructor, as
   * that may result in members-injection methods being called before the constructor has completed,
   * allowing for a partially-constructed instance to escape.
   */
  private void injectIfNecessary() {
    if (needToInject) {
```


この他にも, コアコンポーネントのComponent/Module定義を簡略化できる`@ContributesAndroidInjector`や, コアコンポーネントインスタンスをパラメータにとるProviderメソッドの提供方法などもありますが, 本稿では割愛します.  

ひとまず, `dagger.android`パッケージがどのようなものになる予定なのか, 本稿で大まかにでも掴めたようでしたら幸いです.  
rcがとれて, Dagger2.11が正式リリースされたタイミングで俯瞰図なども描きたいと思います.  

以上です.  
