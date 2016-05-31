package thuytrinh.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;

import rx.subjects.PublishSubject;

public abstract class DisposableViewModel {
  private final PublishSubject<Void> onDispose = PublishSubject.create();

  public PublishSubject<Void> onDispose() {
    return onDispose;
  }

  protected void onCreate(@Nullable Bundle savedInstanceState) {}
}