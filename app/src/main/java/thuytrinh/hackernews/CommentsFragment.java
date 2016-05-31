package thuytrinh.hackernews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import thuytrinh.hackernews.databinding.CommentsBinding;

public class CommentsFragment extends ViewModelFragment {
  @Inject CommentsViewModel viewModel;
  @Inject CommentsAdapter adapter;

  public CommentsFragment() {
    App.component().inject(this);
  }

  public static CommentsFragment newInstance(Intent intent) {
    final CommentsFragment fragment = new CommentsFragment();
    fragment.setArguments(intent.getExtras());
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel.handleArgs(getArguments());
  }

  @Nullable @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final CommentsBinding binding = CommentsBinding.inflate(inflater, container, false);

    final RecyclerView commentsView = binding.commentsView;
    commentsView.setLayoutManager(new LinearLayoutManager(getActivity()));
    commentsView.setAdapter(adapter);

    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override protected DisposableViewModel getViewModel() {
    return viewModel;
  }
}