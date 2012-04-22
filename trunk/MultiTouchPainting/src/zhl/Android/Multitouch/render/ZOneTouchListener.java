package zhl.Android.Multitouch.render;

import zhl.Android.math.Trackball;
import zhl.Android.math.Vector2f;
import zhl.Android.math.Vector3f;
import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZLine3D;
import zhl.Android.scenes.ZObject3D;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class ZOneTouchListener implements OnGestureListener {
	private static final String LOG_TAG = ZOneTouchListener.class.getSimpleName();
	
	private ZView view_;
	private boolean bfortest=false;
	
	public ZOneTouchListener(ZView view) {
		view_ = view;
	}

	public boolean onDown(MotionEvent e) {
		Log.d(LOG_TAG, "onDown()");
		Log.d(LOG_TAG, " down-PointCount:" + e.getPointerCount());
		Log.d(LOG_TAG, " p:" + e.getX() + "," + e.getY()); 

//		ZProjector proj = view_.getProjector();
//		proj.unProject(e.getX(), e.getY());
//		Vector3f v0 = proj.getRayStart();
//		Vector3f v1 = proj.getRayEnd();
//		ZLine3D line = new ZLine3D(v0, v1);
//		Log.d(LOG_TAG, line.toString());
//		
//		ZObject3D obj3D = ZDataManager.getDataManager().getAllObject3D().get(0);
//		boolean b = obj3D.pick(view_.getProjector(), e.getX(), e.getY());
//		Log.d(LOG_TAG, ""+b);
		
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onFling()");
		//return super.onFling(e1, e2, velocityX, velocityY);
		view_.endTransformation();
		view_.getTrackball().end();
		return true;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onLongPress()");
		//super.onLongPress(e);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onScroll()");
		
		// start global rotation
		if (view_.getOperationMode()==ZView.EnumOperationMode.Unknown
				&& e2.getPointerCount()==1 )//&& e1.getPointerCount()==1)
		{
			Trackball tb = view_.getTrackball();
			if (tb==null) return false;
			Vector2f p = new Vector2f(e2.getX(), view_.getHeight() - e2.getY());
			tb.click(p, Trackball.MotionType.Rotation);
			view_.setOperationMode(ZView.EnumOperationMode.GlobalRotation);		
		}
				
		// start global panning
		if (view_.getOperationMode()==ZView.EnumOperationMode.Unknown
				&& e2.getPointerCount()==2 )//&& e2.getPointerCount()==2) 
		{
			Trackball tb = view_.getTrackball();
			if (tb==null) return false;
			Vector2f p = new Vector2f(e2.getX(), view_.getHeight() - e2.getY());
			tb.click(p.times(3.f/view_.getViewportScaleRatio()), Trackball.MotionType.Pan);
			view_.setOperationMode(ZView.EnumOperationMode.GlobalPan);	
		}
		
		// global browsing
		if (view_.getOperationMode()==ZView.EnumOperationMode.GlobalRotation)
		{
			Trackball tb = view_.getTrackball();
			if (tb==null) return false;			
			Vector2f p = new Vector2f(e2.getX(), view_.getHeight() - e2.getY());
			tb.drag(p);
			return true;
		}

		
		// global panning
		if (view_.getOperationMode()==ZView.EnumOperationMode.GlobalPan)
		{
			Log.d(LOG_TAG, " in:GlobalPan");
			Trackball tb = view_.getTrackball();
			if (tb==null) return false;	
			Vector2f p = new Vector2f(e2.getX(), view_.getHeight() - e2.getY());
			tb.drag(p.times(3.f/view_.getViewportScaleRatio()));
			return true;
		}

		// global zooming
		if (view_.getOperationMode()==ZView.EnumOperationMode.GlobalZoom)
		{
			Trackball tb = view_.getTrackball();
			if (tb==null) return false;	
			Vector2f p = new Vector2f(e2.getX(), e2.getY());
			//p *= this.simpleOpenGlControl.Width;
			tb.drag(p.times(view_.getWindowWidth()));
			return true;
		}

		return true;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onShowPress()");
		//super.onShowPress(e);
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onSingleTapUp()");
		return true;
	}

}
