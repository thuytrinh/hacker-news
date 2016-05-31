package thuytrinh.hackernews;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ItemServiceTest {
  @Mock Api api;
  private SharedPreferences preferences;
  private ItemService service;

  @Before public void before() {
    MockitoAnnotations.initMocks(this);
    preferences = RuntimeEnvironment.application.getSharedPreferences("", Context.MODE_PRIVATE);
    service = new ItemService(
        preferences,
        api,
        new Gson()
    );
  }

  @Test public void clearAllPrefs() {
    preferences.edit().putString("topStories", "5,4,3").apply();
    service.clear();
    assertThat(preferences.getAll()).isEmpty();
  }

  @Test public void fetchAndSaveItem() {
    final Item item = ImmutableItem.builder()
        .id(2)
        .time(1L)
        .text("Some text")
        .build();
    final AtomicInteger counter = new AtomicInteger(0);
    when(api.fetchItemAsync(eq(2L))).thenAnswer(new Answer<Observable<Item>>() {
      @Override public Observable<Item> answer(InvocationOnMock invocation) throws Throwable {
        if (counter.getAndIncrement() > 1) {
          return Observable.error(new RuntimeException());
        } else {
          return Observable.just(item).delay(1, TimeUnit.SECONDS);
        }
      }
    });
    final TestSubscriber<Item> s1 = new TestSubscriber<>();
    service.getItemAsync(2L).subscribe(s1);

    s1.awaitTerminalEvent();
    s1.assertNoErrors();
    s1.assertValue(item);

    final TestSubscriber<Item> s2 = new TestSubscriber<>();
    service.getItemAsync(2L).subscribe(s2);

    s2.awaitTerminalEvent();
    s2.assertNoErrors();
    s2.assertValue(item);
  }

  @Test public void fetchAndSaveTopStories() {
    final List<Long> ids = Arrays.asList(1L, 2L);
    final AtomicInteger counter = new AtomicInteger(0);
    when(api.fetchTopStoriesAsync()).thenAnswer(new Answer<Observable<List<Long>>>() {
      @Override public Observable<List<Long>> answer(InvocationOnMock invocation) throws Throwable {
        if (counter.getAndIncrement() > 1) {
          return Observable.error(new RuntimeException());
        } else {
          return Observable.just(ids)
              .delay(1, TimeUnit.SECONDS);
        }
      }
    });
    final TestSubscriber<List<Long>> s1 = new TestSubscriber<>();
    service.getTopStoriesAsync().subscribe(s1);

    s1.awaitTerminalEvent();
    s1.assertNoErrors();
    s1.assertValue(ids);

    final TestSubscriber<List<Long>> s2 = new TestSubscriber<>();
    service.getTopStoriesAsync().subscribe(s2);

    s2.awaitTerminalEvent();
    s2.assertNoErrors();
    s2.assertValue(ids);
  }
}