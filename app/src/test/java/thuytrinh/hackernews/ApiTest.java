package thuytrinh.hackernews;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.observers.TestSubscriber;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ApiTest {
  private MockWebServer server;
  private Api api;

  @Before public void before() {
    server = new MockWebServer();
    api = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api.class);
  }

  @After public void after() throws IOException {
    server.shutdown();
  }

  @Test public void fetchTopStoriesSuccessfully() throws IOException {
    final MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(200);
    mockResponse.setBody(IOUtils.toString(
        getClass().getResourceAsStream("/topstories.json"),
        Charset.defaultCharset()
    ));
    server.enqueue(mockResponse);

    final TestSubscriber<List<Long>> subscriber = new TestSubscriber<>();
    api.fetchTopStoriesAsync().subscribe(subscriber);

    subscriber.assertValue(Arrays.asList(
        11798912L,
        11798646L,
        11798162L
    ));
  }

  @Test public void fetchItemSuccessfully() throws IOException {
    final MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(200);
    mockResponse.setBody(IOUtils.toString(
        getClass().getResourceAsStream("/11798912.json"),
        Charset.defaultCharset()
    ));
    server.enqueue(mockResponse);

    final TestSubscriber<Item> subscriber = new TestSubscriber<>();
    api.fetchItemAsync(11798912).subscribe(subscriber);

    subscriber.assertValue(
        ImmutableItem.builder()
            .id(11798912)
            .by("rafaelc")
            .descendants(10)
            .score(42)
            .time(1464567757)
            .kids(Arrays.asList(11799073L, 11799074L, 11799096L, 11799127L, 11799089L))
            .title("How Not to Explain Success")
            .build()
    );
  }
}