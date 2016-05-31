package thuytrinh.hackernews;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class App extends Application {
  private static AppComponent component;

  static AppComponent component() {
    return component;
  }

  @Override public void onCreate() {
    super.onCreate();
    component = DaggerAppComponent.builder()
        .appModule(new AppModule(this))
        .build();
    JodaTimeAndroid.init(this);
  }
}