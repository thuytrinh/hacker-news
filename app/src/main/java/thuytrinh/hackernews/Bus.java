package thuytrinh.hackernews;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class Bus {
  private final PublishSubject<Object> subject = PublishSubject.create();

  @Inject public Bus() {}

  public <T> void post(T event) {
    subject.onNext(event);
  }

  public Observable<Object> listen() {
    return subject.asObservable();
  }
}