package thuytrinh.hackernews;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import thuytrinh.hackernews.databinding.StoryBinding;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
  private List<ItemViewModel> viewModels;

  @Inject ItemsAdapter() {}

  @BindingAdapter("stories")
  public static void setViewModels(RecyclerView v, ObservableList<ItemViewModel> viewModels) {
    if (v.getAdapter() instanceof ItemsAdapter) {
      final ItemsAdapter adapter = (ItemsAdapter) v.getAdapter();
      adapter.setViewModels(viewModels);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(StoryBinding.inflate(
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

  private void setViewModels(List<ItemViewModel> viewModels) {
    this.viewModels = viewModels;
    notifyDataSetChanged();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private final StoryBinding binding;

    ViewHolder(StoryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void setViewModel(ItemViewModel viewModel) {
      binding.setViewModel(viewModel);
      viewModel.loadItem();
    }
  }
}