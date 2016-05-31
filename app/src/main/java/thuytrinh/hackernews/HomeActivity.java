package thuytrinh.hackernews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, new HomeFragment())
          .commit();
    }
  }
}