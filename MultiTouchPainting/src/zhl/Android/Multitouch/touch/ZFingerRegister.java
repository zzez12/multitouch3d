package zhl.Android.Multitouch.touch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import zhl.Android.Multitouch.render.ZColor;
import zhl.Android.Multitouch.render.ZView;
import zhl.Android.Multitouch.touch.ZFingerRegistration.EnumFinger;
import zhl.Android.Multitouch.touch.ZFingerRegistration.EnumPalm;
import zhl.Android.Multitouch.touch.ZFingerRegistration.TouchTransformationMode;
import zhl.Android.scenes.ZDisk2D;

public class ZFingerRegister {
	
	public static final String LOG_TAG = ZFingerRegister.class.getSimpleName();
	
	public interface FingerRegisterListener{
		void onGroupCreate(Object sender, ZTouchGroup group);
		void onGroupRemove(Object sender, ZTouchGroup group);
		void onGroupMove(Object sender, ZTouchGroup group, ZTouchRecord record);
		void onGroupLongMove(Object sender, ZTouchGroup group, ZTouchRecord record);
		void onGroupStartTransform(Object sender, ZTouchGroup group);
		void onGroupTap(Object sender, ZTouchGroup group);
		void onGroupDoubleTap(Object sender, ZTouchGroup group);
		void onGroupHold(Object sender, ZTouchGroup group);
		void onGroupAddPoint(Object sender, ZTouchGroup group, ZTouchRecord record);
		void onGroupRemovePoint(Object sender, ZTouchGroup group, ZTouchRecord record);
		void onDraw(GL10 gl);
		void onTimerTick(Object sender);
		ZView getView();
	}
	
	public static class EmptyFingerRegisterListener implements FingerRegisterListener{
		public static final String LOG_TAG = EmptyFingerRegisterListener.class.getSimpleName();
		public void onGroupCreate(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupCreate");
		}
		public void onGroupRemove(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupRemove");
		}
		public void onGroupMove(Object sender, ZTouchGroup group,
				ZTouchRecord record) {
			Log.d(LOG_TAG, "onGroupMove");
		}
		public void onGroupLongMove(Object sender, ZTouchGroup group,
				ZTouchRecord record) {
			Log.d(LOG_TAG, "onGroupLongMoveEx");
		}
		public void onGroupStartTransform(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupStartTransform");
		}
		public void onGroupTap(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupTap");
		}
		public void onGroupDoubleTap(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupDoubleTap");
		}
		public void onGroupHold(Object sender, ZTouchGroup group) {
			Log.d(LOG_TAG, "onGroupHold");
		}
		public void onGroupAddPoint(Object sender, ZTouchGroup group,
				ZTouchRecord record) {
			Log.d(LOG_TAG, "onGroupAddPointEx");
		}
		public void onGroupRemovePoint(Object sender, ZTouchGroup group,
				ZTouchRecord record) {
			Log.d(LOG_TAG, "onGroupRemovePointEx");
		}
		public void onDraw(GL10 gl) {}
		public ZView getView() {return null;}
		public void onTimerTick(Object sender) {
			Log.d(LOG_TAG, "onTimerTick");
		}
	}
	
	private ZDisk2D disk2DObj_ = new ZDisk2D(20, 30, 30, 1);
	
	private FingerRegisterListener listener_;
	private ZTimer timer_;
	private Context context_;
	/*
	 * Set of all active touch records, indexed with the record id.
	 */
	//private HashMap<Integer, ZTouchRecord> touchRecords_ = new HashMap<Integer, ZTouchRecord>();
	private Hashtable<Integer, ZTouchRecord> touchRecords_ = new Hashtable<Integer, ZTouchRecord>();
	//private Map<Integer, ZTouchRecord> touchRecords_ = Collections.synchronizedMap(touchRecordsNoWarp_);
	/*
	 * Set of all active touch groups, indexed with the group id.
	 */
	private Hashtable<Integer, ZTouchGroup> touchGroups_ = new Hashtable<Integer, ZTouchGroup>();
	//private Map<Integer, ZTouchGroup> touchGroups_ = Collections.synchronizedMap(touchGroupsNoWarp_);
	
	/*
	 * Maximum distance for grouping touch points.
	 * Default value is 800.
	 */
	public int groupingDistance_ = 800;
	/*
	 * Distance for OnGroupLongMove event occurs.
	 * Default value is 20.
	 */
	public int longMoveDistance_ = 20;
	/*
	 * Maximum distance for determining a touch point is "moved".
	 * Used for generation of OnGroupHold events.
	 * Default value is 30.
	 */
	public int moveDistance_ = 40;		// in pixels
	/*
	 * Maximum tapping time for determining single taps.
	 * Default value is 500 (in millisecond).
	 */
	public int tapTime_ = 500;			// in milliseconds
	/*
	 * Maximum tapping time for determining double taps.
	 * Default value is 800 (in millisecond).
	 */
	public int doubleTapTime = 800;		// in milliseconds
	/*
	 * Minimum holding time for determining holding.
	 * Default value is 500 (in millisecond).
	 */
	public int holdTime_ = 500;			// in milliseconds
	/*
	 * Minimum number of contact fingers to start finger register, value has to be 3, 4, or 5
	 * Default value is 3. 
	 */
	public int numOfFingerToStartRegister_ = 3;
	
	public ZFingerRegister(Context context, ZFingerRegisterListener listener) {
		this.context_ = context;
		initTimer();
		if (listener==null) {
			this.listener_ = new EmptyFingerRegisterListener();
		} else {
			this.listener_ = listener;
		}
		//disk2DObj_.buildMeshData();
	}
	
	private void initTimer() {
		final ZFingerRegister thisPointer = this;
		timer_ = new ZTimer(new ZTimer.ZTimerTick() {
			public void onTimerTick() {
				listener_.onTimerTick(thisPointer);
			}
		});
	}
	
	synchronized public void addTouchPoint(int id, float x, float y) {
		//Log.d(LOG_TAG, "addTouchPoint()");
		// remove old record if exist
		if (this.getTouchRecords().get(id)!=null) {
			this.getTouchRecords().remove(id);
		}
		
		// create and add new touch record
		ZTouchRecord r = new ZTouchRecord(id, x, y, timer_.getCurrentTick());
		this.getTouchRecords().put(id, r);
		
		if (this.bGroupTouchPoints_) {
			float minSqDis = Float.MAX_VALUE;
			ZTouchGroup minGroup = null;
			
			// find closest group		
			for (ZTouchGroup g : this.touchGroups_.values()) {
				// a group has no more than 5 points
				if (g.touchRecords_.size() >= 5) continue;
				
				// reject group with large time difference
				if (r.downTime_ - g.createTime_ > 1000) continue;
				
				// compute distance
				float dx = g.cx_ - r.x_;
				float dy = g.cy_ - r.y_;
				float sqDis = dx*dx + dy*dy;
				float dis = (float)Math.sqrt(sqDis);
				
				// reject group if too far away
				if (dis > groupingDistance_) continue;
				
				if (sqDis < minSqDis) {
					minSqDis = sqDis;
					minGroup = g;
				}
			}
			
			// create new group if in need
			if (minGroup == null) {
				// get next id
				int gid = 0;
				while(this.touchGroups_.containsKey(gid)) gid++;
				minGroup = new ZTouchGroup(gid, r.x_, r.y_, r.downTime_);
				this.touchGroups_.put(gid, minGroup);
				
				// generate group create event
				listener_.onGroupCreate(this, minGroup);
			}
			
			// add touch point to group
			r.group_ = minGroup;
			minGroup.sumX += r.x_;
			minGroup.sumY += r.y_;
			minGroup.touchRecords_.add(r);
			
			// update group center
			updateGroupCenter(minGroup);
			
			// update group center
			listener_.onGroupCreate(this, minGroup);
			
			// detect finger
			if (minGroup.touchRecords_.size() >= numOfFingerToStartRegister_ && bDetectFingers_) {
				detectFingers(minGroup);
			}
			
			// generate group add point event
			listener_.onGroupAddPoint(this, minGroup, r);
			
			// generate group move event
			if (minGroup.touchRecords_.size()>1) {
				listener_.onGroupMove(this, minGroup, r);
			}
		}
	}
	
	synchronized public void moveTouchPoint(int id, float x, float y) {
		ZTouchRecord r = null;
		boolean longMove = false;
		//Log.d(LOG_TAG, "moveTouchPoint() ->(" + id + ", " +x + "," + y + ")");

		if (this.touchRecords_.containsKey(id)) {
			r = this.touchRecords_.get(id);
			
			// process event only if movement greater than 1 pixel
			if (r.x_==x && r.y_==y) return ;
			
			r.prevX_ = r.x_;
			r.prevY_ = r.y_;
			r.x_ = x;
			r.y_ = y;
			
			float dx = r.x_ - r.downX_;
			float dy = r.y_ - r.downY_;
			float sqDis = dx*dx+dy*dy;
			if (sqDis > moveDistance_*moveDistance_) {
				r.moved_ = true;
			}
			
			dx = r.x_ - r.prevX_;
			dy = r.y_ - r.prevY_;
			sqDis = dx*dx + dy*dy;
			float dis = (float)Math.sqrt(sqDis);
			float sp = dis / ((timer_.getCurrentTick() - r.lastListX_)/1000.f);
			sp *= 1000f;
			r.speed_ = r.speed_ * 0.9f + sp * 0.1f;
			r.lastTime_ = timer_.getCurrentTick();
			
			dx = r.lastListX_ - r.prevX_;
			dy = r.lastListY_ - r.prevY_;
			sqDis = dx*dx + dy*dy;
			if (sqDis > moveDistance_*moveDistance_) {
				if (r.prevListX_.size()>=40) r.prevListX_.removeLast();
				if (r.prevListY_.size()>=40) r.prevListY_.removeLast();
				r.prevListX_.addFirst(r.prevX_);
				r.prevListY_.addFirst(r.prevY_);
				r.lastListX_ = r.prevX_;
				r.lastListY_ = r.prevY_;
				longMove = true;
				//Log.d(LOG_TAG, "LongMove! " + sqDis);
			}
		}
		else {
			Log.d(LOG_TAG, "!!Not contained");
		}
		
		// update group center
		if (this.bGroupTouchPoints_) {
			if (r !=null && r.group_!=null) {
				updateGroupCenter(r.group_);
				if (r.moved_) r.group_.moved_ = true;
				// generate group move event
				listener_.onGroupMove(this, r.group_, r);
				if (longMove) listener_.onGroupLongMove(this, r.group_, r);
			}
		}
		
		// update group transformation
		if (r != null) {
			TouchTransformationMode oldMode = r.group_.transformationMode_;
			r.group_.computeTransformation();
			TouchTransformationMode newMode = r.group_.transformationMode_;
			//Log.d(LOG_TAG, "old: " + oldMode + "  new: " + newMode);
			if (oldMode==TouchTransformationMode.Unknown && newMode !=TouchTransformationMode.Unknown) {
				// generate group start transformation event
				listener_.onGroupStartTransform(this, r.group_);
			}
		}
		
//		for (ZTouchRecord rr:this.touchRecords_.values()) {
//			Log.d(LOG_TAG, rr.currentStatus());
//			Log.d(LOG_TAG, rr.group_ + rr.group_.currentStatus());
//		}
	}


	synchronized public void removeTouchPoint(int id, float x, float y) {
		ZTouchRecord r = null;
		
		// remove record
		if (this.touchRecords_.containsKey(id)) {
			r = this.touchRecords_.get(id);
			this.touchRecords_.remove(id);
		}
		
		// remove touch point from group
		if (r!=null && r.group_!=null) {
			ZTouchGroup g = r.group_;
			g.touchRecords_.remove(r);	// may cause ConcurrentModificationException
			g.oldTouchRecords_.add(r);
			
			// generate group remove point event	
			listener_.onGroupRemovePoint(this, g, r);
			
			// remove groud if in need
			if (g.touchRecords_.size()==0) {
				this.touchGroups_.remove(g.id_);	// may cause ConcurrentModificationException

				// generate group  remove event
				listener_.onGroupRemove(this, g);
				
				// generate group tap event
				if (!g.moved_ && timer_.currentTick_ - g.createTime_<this.tapTime_) {
					listener_.onGroupTap(this, g);
					
					// generate group tap event
					if (this.lastTag_!=null && this.lastTag_.oldTouchRecords_.size()==g.oldTouchRecords_.size()
							&& g.createTime_-lastTag_.createTime_<this.doubleTapTime 
							) {//&& ??)
						int cn = g.oldTouchRecords_.size();
						float dx = (lastTag_.sumX-g.sumX)/cn;
						float dy = (lastTag_.sumY-g.sumY)/cn;
						float sqDis = dx*dx + dy*dy;
						if (sqDis<moveDistance_*moveDistance_) {
							listener_.onGroupDoubleTap(this, g);
							
							this.lastTag_ = null;
						}
						else {
							this.lastTag_ = g;
						}
					}
					else {
						// update group center
						updateGroupCenter(r.group_);
						// generate group move event
						listener_.onGroupMove(this, r.group_, null);
					}
				}
			}
		}
		


	}
	
	public void updateHoldTime() {
		for (ZTouchGroup g : touchGroups_.values()) {
			if (g.moved_) continue;
			if (g.hold_) continue;
			
			if (timer_.currentTick_ - g.createTime_ > holdTime_) {
				g.hold_ = true;
				listener_.onGroupHold(this, g);
			}
		}
	}
	
	public void updateSpeed() {
		for (ZTouchGroup g : touchGroups_.values()) {
			for (ZTouchRecord r: g.touchRecords_) {
				r.speed_ *= 0.95;
			}
		}
	}
	
	
	private void updateGroupCenter(ZTouchGroup g) {
		float cx = 0.f;
		float cy = 0.f;
		for (ZTouchRecord r : g.touchRecords_) {
			cx += r.x_;
			cy += r.y_;
		}
		g.cx_ = g.cx_ * 0.f + cx/g.touchRecords_.size();
		g.cy_ = g.cy_ * 0.f + cy/g.touchRecords_.size();
	}
	
	private void detectFingers(ZTouchGroup g) {
		int n = g.touchRecords_.size();
		
		// compute angles corresponding to screen
		for (ZTouchRecord r:g.touchRecords_) {
			r.angle_ = (float)Math.atan2(r.y_-g.cy_, r.x_-g.cx_);
			r.finger_ = EnumFinger.Unknown;
		}
		
		// compute span angles
		List<ZTouchRecord> l = new ArrayList<ZTouchRecord>(this.touchRecords_.values());
		Collections.sort(l, new Comparator<ZTouchRecord>() {

			public int compare(ZTouchRecord arg0, ZTouchRecord arg1) {
				if (arg0.angle_>arg1.angle_)
					return 1;
				else if (arg0.angle_<arg1.angle_)
					return -1;
				else
					return 0;
			}
			
		});
		
		float totAngle = 0.f;
		for (int i=1; i<n; i++) {
			float diff = l.get(i).angle_ - l.get(i).angle_;
			l.get(i).spanAngle_ = diff;
			totAngle += diff;
		}
		l.get(0).spanAngle_ = (float)Math.PI*2 - totAngle;
		
		float tmp = l.get(0).spanAngle_;
		for (int i=0; i<n-1; i++) {
			l.get(i).spanAngle_ += tmp;
		}
		
		// compute area & path lenth
		float area = 0;
		float length = 0;
		for (int i=0; i<n; i++) {
			int j = (i+1)%n;
			area += l.get(i).x_ - l.get(j).x_;
			float dx = l.get(i).x_ - l.get(j).x_;
			float dy = l.get(i).y_ - l.get(j).y_;
			length += (float)Math.sqrt(dx*dx+dy*dy);
		}
		area /= 2.f;
		
		// compute distance to center
		float[] disArray = new float[l.size()];
		for (int i=0; i<n; i++) {
			float dx = l.get(i).x_ - g.cx_;
			float dy = l.get(i).y_ - g.cy_;
			disArray[i] = (float)Math.sqrt(dx*dx+dy*dy);
		}
		Arrays.sort(disArray);
		float d1 = disArray[n-1];
		float d2 = disArray[n-2];
		float dmin = disArray[0];
		
		// find the thumb
		float maxAngle = 0;
		int maxIndex = -1;
		for (int i=0; i<n; i++) {
			if (l.get(i).spanAngle_ > maxAngle) {
				maxAngle = l.get(i).spanAngle_;
				maxIndex = i;
			}
		}
		
		if (d1==0 || d2==0 || d1/d2<fDistanceRatio_) return;
		if (n==4 && area/(length*length)<0.03) return;
		if (n==3 && area/(length*length)<0.01) return;
		
		// determine left or right hand
		ZTouchRecord thumb = l.get(maxIndex);
		ZTouchRecord prev = (maxIndex>0) ? l.get(maxIndex-1) : l.get(n-1);
		ZTouchRecord next = (maxIndex<n-1) ? l.get(maxIndex+1) : l.get(0);
		float dx1 = prev.x_ - thumb.x_;
		float dy1 = prev.y_ - thumb.y_;
		float dx2 = next.x_ - thumb.x_;
		float dy2 = next.y_ - thumb.y_;
		float dis1 = dx1*dx1 + dy1*dy1;
		float dis2 = dx2*dx2 + dy2*dy2;
		g.palm_ = (dis1<dis2) ? EnumPalm.Left : EnumPalm.Right;
		
		// determin all fingers
		int inc = (dis1<dis2) ? n-1 : 1;
		int currIndex = maxIndex;
		int fingerIdx = 1;
		//EnumFinger finger = EnumFinger.values()[fingerIdx];
		while (l.get(currIndex).finger_ == EnumFinger.Unknown) {
			l.get(currIndex).finger_ = EnumFinger.values()[fingerIdx];
			currIndex = (currIndex+inc)%n;
			fingerIdx++;
		}
	}
	
	public Map<Integer, ZTouchRecord> getTouchRecords() {
		return touchRecords_;
	}

	public void setTouchRecords(Hashtable<Integer, ZTouchRecord> touchRecords_) {
		this.touchRecords_ = touchRecords_;
	}

	public Map<Integer, ZTouchGroup> getTouchGroups() {
		return touchGroups_;
	}

	public void setTouchGroups(Hashtable<Integer, ZTouchGroup> touchGroups_) {
		this.touchGroups_ = touchGroups_;
	}
	
	synchronized public void onDraw(GL10 gl) {
		//this.listener_.onDraw(gl);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//synchronized(this) {
			for (ZTouchGroup group : this.touchGroups_.values()) {
				for (ZTouchRecord rec : group.touchRecords_) {
					gl.glPushMatrix();
					gl.glTranslatef(rec.x_, listener_.getView().getHeight()-rec.y_, 0);
					disk2DObj_.draw(gl);
					gl.glPopMatrix();
					
					// draw down position
					if (rec.moved_) {
						//float offset = 0;
						gl.glPushMatrix();
						gl.glTranslatef(rec.downX_, listener_.getView().getHeight()-rec.downY_, 0);
						disk2DObj_.draw(gl);
						gl.glPopMatrix();
					}
					
					// draw path (when ActiveSnap mode)
					// TODO
				}
			}	
		//}
		listener_.onDraw(gl);
	}

	private boolean bGroupTouchPoints_ = true;
	private boolean bDetectFingers_ = false;	// this is not supported now
	private float fDistanceRatio_ = 1.2f;
	private ZTouchGroup lastTag_ = null;
//	private ZTimer timer_ = new ZTimer();


	synchronized public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//Log.d(LOG_TAG, "Motion Down! n:" + event.getPointerCount() + " id:" + event.getPointerId(event.getActionIndex()));
			for (int i=0; i<event.getPointerCount(); i++) {
				int id = event.getPointerId(i);
				this.addTouchPoint(id, event.getX(id), event.getY(id));
			}
			break;
		case MotionEvent.ACTION_MOVE:
			//Log.d(LOG_TAG, "Motion Move! n:" + event.getPointerCount() + " id:" + event.getPointerId(event.getActionIndex()));
			for (int i=0; i<event.getPointerCount(); i++) {
				int id = event.getPointerId(i);
				//Log.d(LOG_TAG, " --> id:" + id + "(" + event.getX(id) + "," + event.getY(id) + ")");
				this.moveTouchPoint(id, event.getX(id), event.getY(id));
			}
			break;
		case MotionEvent.ACTION_UP:
			//Log.d(LOG_TAG, "Motion Up! n:" + event.getPointerCount() + " id:" + event.getPointerId(event.getActionIndex()));
			for (int i=0; i<event.getPointerCount(); i++) {
				int id = event.getPointerId(i);
				this.removeTouchPoint(id, event.getX(id), event.getY(id));
			}
			//currentStatus();
			
			break;
		case MotionEvent.ACTION_POINTER_DOWN:{
			//Log.d(LOG_TAG, "Motion Pointer Down! n:" + event.getPointerCount() + " id:" + event.getPointerId(event.getActionIndex()));
			int id = event.getPointerId(event.getActionIndex());
			this.addTouchPoint(id, event.getX(id), event.getY(id));
			break;		
		}
		case MotionEvent.ACTION_POINTER_UP: {
			//Log.d(LOG_TAG, "Motion Pointer Up! n:" + event.getPointerCount() + " id:" + event.getPointerId(event.getActionIndex()));
			int id = event.getPointerId(event.getActionIndex());
			this.removeTouchPoint(id, event.getX(id), event.getY(id));
			//currentStatus();
			break;
		}

		}
		return false;
	}
	
	public void currentStatus() {
		Log.d(LOG_TAG, "---"+touchGroups_.size());
	}
	
}
