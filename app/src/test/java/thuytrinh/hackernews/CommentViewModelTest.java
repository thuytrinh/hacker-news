package thuytrinh.hackernews;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.subjects.PublishSubject;
import uk.co.ribot.androidboilerplate.util.RxSchedulersOverrideRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentViewModelTest {
  @Rule public RxSchedulersOverrideRule rule = new RxSchedulersOverrideRule();
  @Mock ItemService itemService;
  private CommentViewModel viewModel;

  @Before public void before() {
    MockitoAnnotations.initMocks(this);
    viewModel = new CommentViewModel(itemService);
  }

  @Test public void initially() {
    assertThat(viewModel.isBusy.get()).isFalse();
    assertThat(viewModel.text.get()).isNull();
  }

  @Test public void textIsItemText() {
    viewModel.setId(2L);
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .text("Some text")
        .build();
    when(itemService.getItemAsync(eq(2L)))
        .thenReturn(Observable.just(item));
    viewModel.loadComment();

    assertThat(viewModel.text.get()).isEqualTo(item.text());
  }

  @Test public void busyWhileLoadingComment() {
    final PublishSubject<Item> s = PublishSubject.create();
    when(itemService.getItemAsync(eq(2L))).thenReturn(s);

    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .text("Some text")
        .build();
    assertThat(viewModel.isBusy.get()).isFalse();
    viewModel.setId(2L);
    viewModel.loadComment();
    assertThat(viewModel.isBusy.get()).isTrue();

    s.onNext(item);
    s.onCompleted();
    assertThat(viewModel.isBusy.get()).isFalse();
  }
}