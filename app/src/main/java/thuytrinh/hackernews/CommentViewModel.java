package thuytrinh.hackernews;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import javax.inject.Inject;

import rx.functions.Action1;

public class CommentViewModel extends DisposableViewModel {
  public final ObservableField<String> text = new ObservableField<>();
  public final ObservableBoolean isBusy = new ObservableBoolean();
  private final ItemService itemService;
  private long id;

  @Inject public CommentViewModel(ItemService itemService) {
    this.itemService = itemService;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void loadComment() {
    itemService.getItemAsync(id)
        .takeUntil(onDispose())
        .compose(Utils.<Item>reportStatus(isBusy))
        .subscribe(new Action1<Item>() {
          @Override public void call(Item item) {
            text.set(item.text());
          }
        }, Utils.logError());
  }
}