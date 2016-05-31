package thuytrinh.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;
import thuytrinh.hackernews.databinding.HomeBinding;

public class HomeFragment extends ViewModelFragment {
  @Inject HomeViewModel viewModel;
  @Inject ItemsAdapter itemsAdapter;
  @Inject Bus bus;
  private Subscription subscription;

  public HomeFragment() {
    App.component().inject(this);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    subscription = bus.listen().ofType(CommentsSelectedEvent.class)
        .subscribe(new Action1<CommentsSelectedEvent>() {
          @Override public void call(CommentsSelectedEvent event) {
            startActivity(CommentsActivity.newIntent(getActivity(), event.item()));
          }
        }, Utils.logError());
  }

  @Nullable @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final HomeBinding binding = HomeBinding.inflate(inflater, container, false);

    final RecyclerView storiesView = binding.storiesView;
    storiesView.setLayoutManager(new LinearLayoutManager(getActivity()));
    storiesView.setAdapter(itemsAdapter);

    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }

  @Override protected DisposableViewModel getViewModel() {
    return viewModel;
  }
}