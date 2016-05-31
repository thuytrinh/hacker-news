package thuytrinh.hackernews;

import android.content.res.Resources;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ItemViewModel extends DisposableViewModel {
  public final ObservableBoolean isBusy = new ObservableBoolean();
  public final ObservableField<String> title = new ObservableField<>();
  public final ObservableField<String> subtitle = new ObservableField<>();
  private final Resources resources;
  private final ItemService itemService;
  private final Bus bus;
  private final Provider<DateTime> nowProvider;
  private Item item;
  private long id;

  @Inject ItemViewModel(
      Resources resources,
      ItemService itemService,
      Bus bus,
      Provider<DateTime> nowProvider) {
    this.resources = resources;
    this.itemService = itemService;
    this.bus = bus;
    this.nowProvider = nowProvider;
  }

  public void loadItem() {
    itemService.getItemAsync(id)
        .takeUntil(onDispose())
        .compose(Utils.<Item>reportStatus(isBusy))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Item>() {
          @Override public void call(Item item) {
            setItem(item);
          }
        }, Utils.logError());
  }

  public void selectComments(View v) {
    final List<Long> kids = item.kids();
    if (kids != null) {
      bus.post(ImmutableCommentsSelectedEvent.of(item));
    }
  }

  void setId(long id) {
    this.id = id;
  }

  void setItem(@NonNull Item item) {
    this.item = item;
    title.set(item.title());

    final StringBuilder s = new StringBuilder();
    final int score = item.score();
    if (score != -1) {
      s.append(resources.getQuantityString(R.plurals.scores, score, score)).append(" ");
    }
    final String by = item.by();
    if (by != null) {
      s.append("by").append(" ").append(by);
    }
    final DateTime dateTime = new DateTime(TimeUnit.SECONDS.toMillis(item.time()));
    s.append(" ").append(DateUtils.getRelativeTimeSpanString(
        dateTime.getMillis(),
        nowProvider.get().getMillis(),
        DateUtils.SECOND_IN_MILLIS
    ));
    final int descendants = item.descendants();
    if (descendants > 0) {
      s.append(" | ").append(resources.getQuantityString(R.plurals.comments, descendants, descendants));
    }
    subtitle.set(s.toString());
  }
}