package thuytrinh.hackernews;

import android.os.Parcel;
import android.os.Parcelable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ItemTest {
  static <T extends Parcelable> Parcel parcel(T original) {
    final Parcel parcel = Parcel.obtain();
    original.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    return parcel;
  }

  @Test public void parcel() {
    final Item expected = ImmutableItem.builder()
        .id(1234)
        .by("Someone")
        .descendants(10)
        .score(42)
        .time(1464567757)
        .kids(Arrays.asList(1L, 2L, 3L))
        .title("Some title")
        .text("Some text")
        .build();
    final Item actual = Item.CREATOR.createFromParcel(parcel(expected));
    assertThat(actual).isEqualTo(expected);
  }

  @Test public void defaultScore() {
    final Item item = ImmutableItem.builder()
        .id(123).time(1234)
        .build();
    assertThat(item.score()).isEqualTo(-1);
  }

  @Test public void defaultDescendants() {
    final Item item = ImmutableItem.builder()
        .id(123).time(1234)
        .build();
    assertThat(item.descendants()).isEqualTo(-1);
  }
}