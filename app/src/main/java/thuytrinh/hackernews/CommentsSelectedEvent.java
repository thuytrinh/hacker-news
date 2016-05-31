package thuytrinh.hackernews;

import org.immutables.value.Value;

@Value.Immutable(builder = false)
interface CommentsSelectedEvent {
  @Value.Parameter Item item();
}