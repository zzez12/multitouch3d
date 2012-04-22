package zhl.Android.Multitouch.touch;

import java.util.Comparator;
import java.util.LinkedList;

import zhl.Android.Multitouch.touch.ZFingerRegistration.EnumFinger;

/**
 * Record of touch points, contains basic information includes contact time and location
 * @author Hanlin
 *
 */
public class ZTouchRecord{

	/*
	 * ID of touch record
	 */
	public int id_;
	/*
	 * Initial contact location (x-coordinate).
	 */
	public float downX_;
	/*
	 * Initial contact location (y-coordinate).
	 */
	public float downY_;
	/*
	 * Previous contact location (x-coordinate).
	 */
	public float prevX_;
	/*
	 * Previous contact location (y-coordinate).
	 */
	public float prevY_;
	/*
	 * Current contact location (x-coordinate).
	 */
	public float x_;
	/*
	 * Current contact location (y-coordinate).
	 */
	public float y_;
	/*
	 * Previous 20 contact locations (x-coordinate).
	 */
	public LinkedList<Float> prevListX_;
	/*
	 * Previous 20 contact locations (y-coordinate).
	 */
	public LinkedList<Float> prevListY_;
	/*
	 * Movinf speed of contact point
	 */
	public float speed_ = 0.f;
	/*
	 * Flag indicates the touching finger is moved after first contact.
	 * Initial value is false.
	 */
	public boolean moved_ = false;
	/*
	 * Reference to the correspondence TouchGroup object. 
	 */
	public ZTouchGroup group_ = null;
	/*
	 * Initial contact time (measured in millisecond).
	 */
	public long downTime_;
	/*
	 * Last update time (measured in millisecond).
	 */
	public long lastTime_;
	public EnumFinger finger_ = EnumFinger.Unknown;
	/*
	 * A reference tag to any other extra data.
	 * Default value is null.
	 */
	public Object tag_ = null;
	
	// internal fields
	public float angle_;
	public float spanAngle_;
	public float lastListX_;
	public float lastListY_;
	
	/**
	 * Constructor
	 * @param id	ID of the touch point
	 * @param x		x-coordinate of the touch location
	 * @param y		y-coordinate of the touch location
	 * @param time 	contact time measured in millisecond
	 */
	public ZTouchRecord(int id, float x, float y, long time) {
		this.id_ = id;
		this.x_ = this.prevX_ = this.downX_ = x;
		this.y_ = this.prevY_ = this.downY_ = y;
		this.lastTime_ = this.downTime_ = time;
		this.prevListX_ = new LinkedList<Float>();
		this.prevListY_ = new LinkedList<Float>();
	}
	
	/*
	 * Clear previous contact locations
	 * It will clear prevListX and prevListY
	 */
	public void clear() {
		this.downX_ = this.prevX_ = this.x_;
		this.downY_ = this.prevY_ = this.y_;
		this.downTime_ = this.lastTime_;
		this.prevListX_ = new LinkedList<Float>();
		this.prevListY_ = new LinkedList<Float>();
	}
	
	public String currentStatus() {
		String str = "";
		str += " recId:" + this.id_;
		str += " down:(" + this.downX_ + "," + this.downY_ + ")"; 
		str += " cur:(" + this.x_ + "," + this.y_ + ")";
		return str;
	}

//	public int compare(Object lhs, Object rhs) {
//		// TODO Auto-generated method stub
//		if ((lhs instanceof ZTouchRecord)
//			&& (rhs instanceof ZTouchRecord) ) {
//			ZTouchRecord ltr = (ZTouchRecord)lhs;
//			ZTouchRecord rtr = (ZTouchRecord)rhs;
////			return ltr.finger_.compareTo(rtr.finger_);
//			if (ltr.finger_.ordinal()>rtr.finger_.ordinal()) return 1;
//			else if (ltr.finger_.ordinal()<rtr.finger_.ordinal()) return -1;
//			else return compare(ltr.downTime_, rtr.downTime_);
//		}
//		return -1;
//	}

//	public int compare(ZTouchRecord lhs, ZTouchRecord rhs) {
//		// TODO Auto-generated method stub
//		if (lhs==null || rhs==null) return -1;
//		if (lhs.finger_.ordinal()>rhs.finger_.ordinal()) return 1;
//		else if (lhs.finger_.ordinal()<rhs.finger_.ordinal()) return -1;
//		//else return lhs.downTime_>rhs.downTime_ ? 1 : (lhs.downTime_<rhs.downTime_ ? -1 : 0);
//		else return 0;
//	}
}

