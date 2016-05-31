package thuytrinh.hackernews;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

class ItemService {
  private final SharedPreferences preferences;
  private final Api api;
  private final Gson gson;

  @Inject ItemService(SharedPreferences preferences, Api api, Gson gson) {
    this.preferences = preferences;
    this.api = api;
    this.gson = gson;
  }

  void clear() {
    preferences.edit().clear().apply();
  }

  Observable<List<Long>> getTopStoriesAsync() {
    final Observable<List<Long>> disk = Observable
        .create(new Observable.OnSubscribe<List<Long>>() {
          @Override public void call(Subscriber<? super List<Long>> subscriber) {
            final String topStories = preferences.getString("topStories", null);
            if (topStories != null) {
              final String[] stories = topStories.split(",");
              final ArrayList<Long> ids = new ArrayList<>();
              for (String id : stories) {
                ids.add(Long.parseLong(id));
              }
              subscriber.onNext(ids);
            }
            subscriber.onCompleted();
          }
        })
        .subscribeOn(Schedulers.io());
    final Observable<List<Long>> remote = api.fetchTopStoriesAsync()
        .doOnNext(new Action1<List<Long>>() {
          @Override public void call(List<Long> ids) {
            preferences.edit()
                .putString("topStories", TextUtils.join(",", ids))
                .apply();
          }
        });
    return Observable.concat(disk, remote).first();
  }

  Observable<Item> getItemAsync(final long id) {
    final Observable<Item> disk = Observable
        .create(new Observable.OnSubscribe<Item>() {
          @Override public void call(Subscriber<? super Item> subscriber) {
            final String item = preferences.getString(String.valueOf(id), null);
            if (item != null) {
              subscriber.onNext(gson.fromJson(item, Item.class));
            }
            subscriber.onCompleted();
          }
        })
        .subscribeOn(Schedulers.io());
    final Observable<Item> remote = api.fetchItemAsync(id)
        .doOnNext(new Action1<Item>() {
          @Override public void call(Item item) {
            preferences.edit()
                .putString(String.valueOf(item.id()), gson.toJson(item))
                .apply();
          }
        });
    return Observable.concat(disk, remote).first();
  }
}