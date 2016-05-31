package thuytrinh.hackernews;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import thuytrinh.hackernews.databinding.CommentBinding;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
  private List<CommentViewModel> viewModels;

  @Inject CommentsAdapter() {}

  @BindingAdapter("comments")
  public static void setViewModels(RecyclerView v, ObservableList<CommentViewModel> viewModels) {
    if (v.getAdapter() instanceof CommentsAdapter) {
      final CommentsAdapter adapter = (CommentsAdapter) v.getAdapter();
      adapter.setViewModels(viewModels);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(CommentBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
    ));
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setViewModel(viewModels.get(position));
  }

  @Override public int getItemCount() {
    return viewModels != null ? viewModels.size() : 0;
  }

  private void setViewModels(List<CommentViewModel> viewModels) {
    this.viewModels = viewModels;
    notifyDataSetChanged();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private final CommentBinding binding;

    ViewHolder(CommentBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void setViewModel(CommentViewModel viewModel) {
      binding.setViewModel(viewModel);
      viewModel.loadComment();
    }
  }
}