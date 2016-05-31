package thuytrinh.hackernews;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentsSelectedEventTest {
  @Test(expected = NullPointerException.class)
  public void itemIsNonNull() {
    ImmutableCommentsSelectedEvent.of(null);
  }
}