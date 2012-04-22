package zhl.Android.Multitouch.paint;

import zhl.Android.Multitouch.render.ZView;
import android.app.Activity;
import android.os.Bundle;

public class MultiTouchPaintingActivity extends Activity {
	private static final String LOG_TAG = MultiTouchPaintingActivity.class.getSimpleName();
	private ZView zView_;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        
        zView_ = new ZView(this);
        setContentView(zView_);
    }
}