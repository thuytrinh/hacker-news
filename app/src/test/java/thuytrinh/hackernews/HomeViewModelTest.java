package thuytrinh.hackernews;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Provider;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class HomeViewModelTest {
  @Mock ItemService itemService;
  @Mock ItemViewModelProvider itemViewModelProvider;
  private HomeViewModel viewModel;

  @Before public void before() {
    MockitoAnnotations.initMocks(this);
    viewModel = new HomeViewModel(itemService, itemViewModelProvider);
  }

  @Test public void clearAndReloadTopStories() {
    when(itemService.getTopStoriesAsync())
        .thenReturn(Observable.just(Collections.<Long>emptyList()));
    viewModel.onRefresh();
    verify(itemService).clear();
    verify(itemService).getTopStoriesAsync();
  }

  @Test public void stopLoadingItemsWhenDisposing() {
    final ItemViewModel a = mock(ItemViewModel.class);
    final PublishSubject<Void> onDisposeA = PublishSubject.create();
    final TestSubscriber<Void> aSubscriber = new TestSubscriber<>();
    onDisposeA.subscribe(aSubscriber);
    when(a.onDispose()).thenReturn(onDisposeA);

    final ItemViewModel b = mock(ItemViewModel.class);
    final PublishSubject<Void> onDisposeB = PublishSubject.create();
    final TestSubscriber<Void> bSubscriber = new TestSubscriber<>();
    onDisposeB.subscribe(bSubscriber);
    when(b.onDispose()).thenReturn(onDisposeB);

    viewModel.stories.addAll(Arrays.asList(a, b));
    viewModel.onDispose().onNext(null);

    verify(a).onDispose();
    verify(b).onDispose();
    aSubscriber.assertValueCount(1);
    bSubscriber.assertValueCount(1);
  }

  /* Because Mockito can't mock Provider<T>! */
  static abstract class ItemViewModelProvider implements Provider<ItemViewModel> {}
}