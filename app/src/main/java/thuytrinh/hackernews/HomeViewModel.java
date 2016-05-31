package thuytrinh.hackernews;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class HomeViewModel extends DisposableViewModel implements SwipeRefreshLayout.OnRefreshListener {
  public final ObservableBoolean isBusy = new ObservableBoolean();
  public final ObservableList<ItemViewModel> stories = new ObservableArrayList<>();
  private final ItemService itemService;
  private final Provider<ItemViewModel> itemViewModelProvider;

  @Inject HomeViewModel(
      ItemService itemService,
      Provider<ItemViewModel> itemViewModelProvider) {
    this.itemService = itemService;
    this.itemViewModelProvider = itemViewModelProvider;

    // To terminate loading/fetching items.
    onDispose().subscribe(new Action1<Void>() {
      @Override public void call(Void unused) {
        for (int i = 0; i < stories.size(); i++) {
          stories.get(i).onDispose().onNext(null);
        }
      }
    }, Utils.logError());
  }

  @Override public void onRefresh() {
    itemService.clear();
    loadTopStories();
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    loadTopStories();
  }

  Observable<List<ItemViewModel>> loadTopStoriesAsync() {
    return itemService.getTopStoriesAsync()
        .flatMap(new Func1<List<Long>, Observable<Long>>() {
          @Override public Observable<Long> call(List<Long> ids) {
            return Observable.from(ids);
          }
        })
        .map(new Func1<Long, ItemViewModel>() {
          @Override public ItemViewModel call(Long id) {
            final ItemViewModel viewModel = itemViewModelProvider.get();
            viewModel.setId(id);
            return viewModel;
          }
        })
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(Utils.<List<ItemViewModel>>reportStatus(isBusy));
  }

  private void loadTopStories() {
    loadTopStoriesAsync()
        .takeUntil(onDispose())
        .subscribe(new Action1<List<ItemViewModel>>() {
          @Override public void call(List<ItemViewModel> newStories) {
            stories.clear();
            stories.addAll(newStories);
          }
        }, Utils.logError());
  }
}