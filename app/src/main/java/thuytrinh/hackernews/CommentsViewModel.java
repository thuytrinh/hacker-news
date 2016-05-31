package thuytrinh.hackernews;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class CommentsViewModel extends DisposableViewModel {
  public final ObservableField<String> title = new ObservableField<>();
  public final ObservableList<CommentViewModel> comments = new ObservableArrayList<>();
  private final Provider<CommentViewModel> commentViewModelProvider;

  @Inject CommentsViewModel(Provider<CommentViewModel> commentViewModelProvider) {
    this.commentViewModelProvider = commentViewModelProvider;

    // To terminate loading/fetching comments.
    onDispose().subscribe(new Action1<Void>() {
      @Override public void call(Void unused) {
        for (int i = 0; i < comments.size(); i++) {
          comments.get(i).onDispose().onNext(null);
        }
      }
    }, Utils.logError());
  }

  public void handleArgs(Bundle args) {
    final Item item = args.getParcelable("item");
    title.set(item.title());
    Observable.from(item.kids())
        .map(new Func1<Long, CommentViewModel>() {
          @Override public CommentViewModel call(Long id) {
            final CommentViewModel viewModel = commentViewModelProvider.get();
            viewModel.setId(id);
            return viewModel;
          }
        })
        .toList()
        .subscribe(new Action1<List<CommentViewModel>>() {
          @Override public void call(List<CommentViewModel> viewModels) {
            comments.clear();
            comments.addAll(viewModels);
          }
        }, Utils.logError());
  }
}