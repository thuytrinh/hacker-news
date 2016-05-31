package thuytrinh.hackernews;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

interface Api {
  @GET("topstories.json") Observable<List<Long>> fetchTopStoriesAsync();
  @GET("item/{id}.json") Observable<Item> fetchItemAsync(@Path("id") long id);
}