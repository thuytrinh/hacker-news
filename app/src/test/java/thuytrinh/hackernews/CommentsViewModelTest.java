package thuytrinh.hackernews;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Provider;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import uk.co.ribot.androidboilerplate.util.RxSchedulersOverrideRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentsViewModelTest {
  @Rule public RxSchedulersOverrideRule rule = new RxSchedulersOverrideRule();
  @Mock CommentViewModelProvider commentViewModelProvider;
  private CommentsViewModel viewModel;

  @Before public void before() {
    MockitoAnnotations.initMocks(this);
    viewModel = new CommentsViewModel(commentViewModelProvider);
  }

  @Test public void titleIsItemTitle() {
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .score(4)
        .title("Some title")
        .by("A")
        .kids(Collections.<Long>emptyList())
        .descendants(10)
        .build();
    final Bundle args = new Bundle();
    args.putParcelable("item", item);
    viewModel.handleArgs(args);
    assertThat(viewModel.title.get()).isEqualTo(item.title());
  }

  @Test public void createCorrespondingViewModelsForKids() {
    when(commentViewModelProvider.get())
        .thenReturn(mock(CommentViewModel.class));

    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .score(4)
        .title("Some title")
        .by("A")
        .kids(Arrays.asList(1L, 2L))
        .descendants(10)
        .build();
    final Bundle args = new Bundle();
    args.putParcelable("item", item);
    viewModel.handleArgs(args);

    assertThat(viewModel.comments).hasSize(2);
  }

  @Test public void stopLoadingCommentsWhenDisposing() {
    final CommentViewModel a = mock(CommentViewModel.class);
    final PublishSubject<Void> onDisposeA = PublishSubject.create();
    final TestSubscriber<Void> aSubscriber = new TestSubscriber<>();
    onDisposeA.subscribe(aSubscriber);
    when(a.onDispose()).thenReturn(onDisposeA);

    final CommentViewModel b = mock(CommentViewModel.class);
    final PublishSubject<Void> onDisposeB = PublishSubject.create();
    final TestSubscriber<Void> bSubscriber = new TestSubscriber<>();
    onDisposeB.subscribe(bSubscriber);
    when(b.onDispose()).thenReturn(onDisposeB);

    viewModel.comments.addAll(Arrays.asList(a, b));
    viewModel.onDispose().onNext(null);

    verify(a).onDispose();
    verify(b).onDispose();
    aSubscriber.assertValueCount(1);
    bSubscriber.assertValueCount(1);
  }

  /* Because Mockito can't mock Provider<T>! */
  static abstract class CommentViewModelProvider implements Provider<CommentViewModel> {}
}