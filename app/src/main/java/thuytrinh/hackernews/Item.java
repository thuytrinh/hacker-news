package thuytrinh.hackernews;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersItem.class)
public abstract class Item implements Parcelable {
  public static final Creator<Item> CREATOR = new Creator<Item>() {
    @Override public Item createFromParcel(Parcel in) {
      final long id = in.readLong();
      final String title = in.readString();
      final String by = in.readString();
      final long time = in.readLong();
      final String text = in.readString();
      final List<Long> kids = new ArrayList<>();
      in.readList(kids, List.class.getClassLoader());
      final int score = in.readInt();
      final int descendants = in.readInt();
      return ImmutableItem.builder()
          .id(id)
          .title(title)
          .by(by)
          .time(time)
          .text(text)
          .kids(kids)
          .score(score)
          .descendants(descendants)
          .build();
    }

    @Override public Item[] newArray(int size) {
      return new Item[size];
    }
  };

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id());
    dest.writeString(title());
    dest.writeString(by());
    dest.writeLong(time());
    dest.writeString(text());
    dest.writeList(kids());
    dest.writeInt(score());
    dest.writeInt(descendants());
  }

  @Override public int describeContents() {
    return 0;
  }

  public abstract long id();
  public abstract @Nullable String title();
  public abstract @Nullable String by();
  public abstract long time();
  public abstract @Nullable List<Long> kids();
  public abstract @Nullable String text();

  @Value.Default public int score() { return -1; }

  @Value.Default public int descendants() { return -1; }
}