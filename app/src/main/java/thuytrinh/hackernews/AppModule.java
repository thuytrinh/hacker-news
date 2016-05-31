package thuytrinh.hackernews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
class AppModule {
  private final Context context;

  AppModule(@NonNull Context context) {
    this.context = context;
  }

  @Provides @Singleton Resources resources() {
    return context.getResources();
  }

  @Provides @Singleton Gson gson() {
    return new Gson();
  }

  @Provides @Singleton Api api(Gson gson) {
    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    final OkHttpClient httpClient = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();
    return new Retrofit.Builder()
        .baseUrl("https://hacker-news.firebaseio.com/v0/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()
        .create(Api.class);
  }

  @Provides @Singleton SharedPreferences preferences() {
    return context.getSharedPreferences("database", Context.MODE_PRIVATE);
  }

  @Provides DateTime now() {
    return DateTime.now();
  }
}