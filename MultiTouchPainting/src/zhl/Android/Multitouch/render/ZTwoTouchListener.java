package zhl.Android.Multitouch.render;

import zhl.Android.math.Trackball;
import zhl.Android.math.Vector2f;
import android.util.Log;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;


public class ZTwoTouchListener implements OnScaleGestureListener {
	private static final String LOG_TAG = ZTwoTouchListener.class.getSimpleName();
	
	private static final float eps_float = 0.008f;
	
	private ZView view_ = null;
	private Vector2f prevPt_ = new Vector2f();

	public ZTwoTouchListener(ZView zView) {
		// TODO Auto-generated constructor stub
		view_ = zView;
	}

	public boolean onScale(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onScale");
		
		ScaleGestureDetector gd = view_.getScaleDetect();
		float cs = gd.getCurrentSpan();
		long et = gd.getEventTime();
		float sf = gd.getScaleFactor();
		Log.d(LOG_TAG, "  CurrentSpan: " + cs);
		Log.d(LOG_TAG, "  EventTime: " + et);
		Log.d(LOG_TAG, "  ScaleFactor: " + sf);
		
		Trackball tb = view_.getTrackball();
		if (view_.getOperationMode()==ZView.EnumOperationMode.Unknown) {
			if (Math.abs(sf-1.f)<eps_float) {
				// start panning
				Vector2f p = new Vector2f(detector.getFocusX(), view_.getHeight() - detector.getFocusY());
				view_.setOperationMode(ZView.EnumOperationMode.GlobalPan);
				tb.click(p.times(3.f/view_.getViewportScaleRatio()), Trackball.MotionType.Pan);
				return true;
			} 
			else {
				// start zooming
				view_.setOperationMode(ZView.EnumOperationMode.GlobalZoom);	
				tb.scale(sf);
				return true;
			}
		}
		else if (view_.getOperationMode()==ZView.EnumOperationMode.GlobalPan) {
			// panning
			Vector2f p = new Vector2f(detector.getFocusX(), view_.getHeight() - detector.getFocusY());
			tb.drag(p.times(3.f/view_.getViewportScaleRatio()));
			return true;
		}
		else if (view_.getOperationMode()==ZView.EnumOperationMode.GlobalZoom) {
			tb.scale(sf);
			return true;
		}
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onScaleBegin");
		prevPt_.x_ = detector.getFocusX();
		prevPt_.y_ = detector.getFocusY();
		return true;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onScaleEnd");
		view_.endTransformation();
	}


}
