package thuytrinh.hackernews;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import rx.subjects.PublishSubject;
import uk.co.ribot.androidboilerplate.util.RxSchedulersOverrideRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ItemViewModelTest {
  @Rule public RxSchedulersOverrideRule rule = new RxSchedulersOverrideRule();
  @Mock ItemService itemService;
  @Mock Bus bus;
  @Mock NowProvider nowProvider;
  private ItemViewModel viewModel;

  @Before public void before() {
    MockitoAnnotations.initMocks(this);
    viewModel = new ItemViewModel(
        RuntimeEnvironment.application.getResources(),
        itemService,
        bus,
        nowProvider
    );
    when(nowProvider.get()).thenReturn(DateTime.now());
  }

  @Test public void initially() {
    assertThat(viewModel.isBusy.get()).isFalse();
    assertThat(viewModel.title.get()).isNull();
    assertThat(viewModel.subtitle.get()).isNull();
  }

  @Test public void showComments() {
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .kids(Arrays.asList(1L, 2L))
        .build();
    viewModel.setItem(item);
    viewModel.selectComments(null);
    verify(bus).post(eq(ImmutableCommentsSelectedEvent.of(item)));
  }

  @Test public void doNotShowComments() {
    viewModel.setItem(
        ImmutableItem.builder()
            .id(2)
            .time(1L)
            .kids(null)
            .build()
    );
    viewModel.selectComments(null);
    verify(bus, times(0)).post(any(CommentsSelectedEvent.class));
  }

  @Test public void titleIsItemTitle() {
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .title("Some title")
        .build();
    viewModel.setItem(item);
    assertThat(viewModel.title.get()).isEqualTo(item.title());
  }

  @Test public void subtitleWithScoresAuthorAndCommentCount() {
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .score(4)
        .title("Some title")
        .by("A")
        .descendants(10)
        .build();
    viewModel.setItem(item);
    assertThat(viewModel.subtitle.get()).contains("4 scores by A");
    assertThat(viewModel.subtitle.get()).endsWith(" | 10 comments");
  }

  @Test public void subtitleWithTimeSpanInHours() {
    final DateTime itemTime = new DateTime(2016, 1, 1, 3, 0);
    final DateTime now = new DateTime(2016, 1, 1, 5, 0);
    when(nowProvider.get()).thenReturn(now);
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(TimeUnit.MILLISECONDS.toSeconds(itemTime.getMillis()))
        .score(4)
        .title("Some title")
        .by("A")
        .descendants(10)
        .build();
    viewModel.setItem(item);
    assertThat(viewModel.subtitle.get())
        .isEqualTo("4 scores by A 2 hours ago | 10 comments");
  }

  @Test public void subtitleWithTimeSpanInMins() {
    final DateTime itemTime = new DateTime(2016, 1, 1, 5, 30);
    final DateTime now = new DateTime(2016, 1, 1, 5, 40);
    when(nowProvider.get()).thenReturn(now);
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(TimeUnit.MILLISECONDS.toSeconds(itemTime.getMillis()))
        .score(4)
        .title("Some title")
        .by("A")
        .descendants(10)
        .build();
    viewModel.setItem(item);
    assertThat(viewModel.subtitle.get())
        .isEqualTo("4 scores by A 10 minutes ago | 10 comments");
  }

  @Test public void terminateLoadingItemWhenDisposing() {
    final PublishSubject<Item> s = PublishSubject.create();
    when(itemService.getItemAsync(eq(2L))).thenReturn(s);

    viewModel.setId(2L);
    viewModel.loadItem();
    assertThat(s.hasObservers()).isTrue();
    s.onNext(
        ImmutableItem.builder()
            .id(2)
            .time(1L)
            .score(4)
            .title("Some title")
            .by("A")
            .descendants(10)
            .build()
    );
    viewModel.onDispose().onNext(null);
    assertThat(s.hasObservers()).isFalse();
  }

  @Test public void busyWhileLoadingItem() {
    final PublishSubject<Item> s = PublishSubject.create();
    when(itemService.getItemAsync(eq(2L))).thenReturn(s);

    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .text("Some text")
        .build();
    assertThat(viewModel.isBusy.get()).isFalse();
    viewModel.setId(2L);
    viewModel.loadItem();
    assertThat(viewModel.isBusy.get()).isTrue();

    s.onNext(item);
    s.onCompleted();
    assertThat(viewModel.isBusy.get()).isFalse();
  }

  /* Because Mockito can't mock Provider<T>! */
  static abstract class NowProvider implements Provider<DateTime> {}
}