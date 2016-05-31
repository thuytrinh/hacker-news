package thuytrinh.hackernews;

import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  Resources resources();
  void inject(HomeFragment fragment);
  void inject(CommentsFragment fragment);
}