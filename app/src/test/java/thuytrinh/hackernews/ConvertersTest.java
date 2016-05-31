package thuytrinh.hackernews;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static thuytrinh.hackernews.Converters.convertBooleanToViewVisibility;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConvertersTest {
  @Test public void goneIfFalse() {
    assertThat(convertBooleanToViewVisibility(false)).isEqualTo(View.GONE);
  }

  @Test public void visibleIfTrue() {
    assertThat(convertBooleanToViewVisibility(true)).isEqualTo(View.VISIBLE);
  }
}