package thuytrinh.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class ViewModelFragment extends Fragment {
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getViewModel().onCreate(savedInstanceState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    getViewModel().onDispose().onNext(null);
  }

  protected abstract DisposableViewModel getViewModel();
}