package thuytrinh.hackernews;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

@BindingMethods({@BindingMethod(
    type = SwipeRefreshLayout.class,
    attribute = "android:onRefresh",
    method = "setOnRefreshListener"
)})
public final class Bindings {
  private Bindings() {}

  @BindingAdapter("htmlText")
  public static void setHtmlText(TextView v, String s) {
    v.setText(TextUtils.isEmpty(s) ? null : Html.fromHtml(s));
  }

  @BindingAdapter("refreshing")
  public static void setRefreshing(final SwipeRefreshLayout v, final boolean refreshing) {
    v.post(new Runnable() {
      @Override public void run() {
        v.setRefreshing(refreshing);
      }
    });
  }
}