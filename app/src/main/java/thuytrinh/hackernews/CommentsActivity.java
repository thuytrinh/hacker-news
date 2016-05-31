package thuytrinh.hackernews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class CommentsActivity extends AppCompatActivity {
  static Intent newIntent(Context context, Item item) {
    return new Intent(context, CommentsActivity.class)
        .putExtra("item", item);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, CommentsFragment.newInstance(getIntent()))
          .commit();
    }
  }
}