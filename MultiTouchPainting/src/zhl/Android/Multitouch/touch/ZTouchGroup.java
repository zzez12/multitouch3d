package zhl.Android.Multitouch.touch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.util.Log;

import zhl.Android.Multitouch.touch.ZFingerRegistration.EnumPalm;
import zhl.Android.Multitouch.touch.ZFingerRegistration.TouchTransformationMode;
import zhl.Android.math.Matrix2f;
import zhl.Android.math.Vector2f;

public class ZTouchGroup {
	public static final String LOG_TAG = ZTouchGroup.class.getSimpleName();
	
	/*
	 * ID of touch record.
	 */
	public int id_;

	/*
	 * Center location of all touch points (x-coordinate).
	 */
	public float cx_;

	/*
	 * Center location of all touch points (y-coordinate).
	 */ 
	public float cy_;

	/*
	 * Flag indicates the touching group is moved after first contact.
	 * Initial value is false.
	 */
	public boolean moved_ = false;

	/*
	 * Flag indicates the touching group is holding after first contact.
	 * Initial value is false.
	 */
	public boolean hold_ = false;
	
	/*
	 * Set of current (active) touch records 
	 */

	public class TRComparator implements Comparator<ZTouchRecord> {

		public int compare(ZTouchRecord lhs, ZTouchRecord rhs) {
			if (lhs==null || rhs==null) return -1;
			if (lhs.finger_.ordinal()>rhs.finger_.ordinal()) return 1;
			else if (lhs.finger_.ordinal()<rhs.finger_.ordinal()) return -1;
			//else return lhs.downTime_>rhs.downTime_ ? 1 : (lhs.downTime_<rhs.downTime_ ? -1 : 0);
			else return 0;	
		}
		
	}
	
	public class TRComparatorClassic implements Comparator<ZTouchRecord> {

		public int compare(ZTouchRecord lhs, ZTouchRecord rhs) {
			if (lhs.id_ < rhs.id_) return -1;
			else if (lhs.id_ > rhs.id_) return 1;
			else return 0;
//			if (lhs.downTime_<rhs.downTime_) return -1;
//			else if (lhs.downTime_>rhs.downTime_) return 1;
//			else return 0;
		}
		
	}
	public TreeSet<ZTouchRecord> touchRecordsNoWarp_ = new TreeSet<ZTouchRecord>(new TRComparatorClassic());
	public SortedSet<ZTouchRecord> touchRecords_ = Collections.synchronizedSortedSet(touchRecordsNoWarp_);
	
	/*
	 * List of disposed touch records (i.e. released touch points), sorted with the order of dispose time.
	 */
	public List<ZTouchRecord> oldTouchRecords_ = new ArrayList<ZTouchRecord>();
	
	/* 
	 * Creation time of the touch group (measured in millisecond).
	 * i.e. the initial contact time of the first touch point of the touch group.
	 */ 
	public long createTime_;
	
	/*
	 * Registered finger. Initial value is EnumPalm.Unknown.
	 */
	public EnumPalm palm_ = ZFingerRegistration.EnumPalm.Unknown;
	
	/*
	 * 
	 */
	public float translationScore_ = 0.f;
	
	/*
	 * 
	 */
	public float scalingScore_ = 0.f;
	
	/*
	 * 
	 */
	public float rotationScore_ = 0.f;
	
	/*
	 * 
	 */
	public float scalingRatio_ = 0.f;
	
	/*
	 * 
	 */
	public TouchTransformationMode transformationMode_ = TouchTransformationMode.Unknown;
	
	/*
	 * A reference tag to any other extra data.
	 * Default value is null.
	 */
	public Object tag_ = null;
	
	private Matrix2f transformation_ = Matrix2f.identityMatrix();
	public float sumX = 0.f;
	public float sumY = 0.f;
	
	public ZTouchGroup(int id, float cx, float cy, long time) {
		this.id_ = id;
		this.cx_ = cx;
		this.cy_ = cy;
		this.createTime_ = time;
	}
	
	/*
	 * Compute the transformation based on the initial contact locations
	 * and current locations of the active (non-released) touch points.
	 */
	public void computeTransformation() {
		Log.d(LOG_TAG, currentStatusTouchRecords());
		// TODO: unfinished!
		// compute center of touch points
		Vector2f c1 = new Vector2f(); // down center
		Vector2f c2 = new Vector2f(); // current center
		for (ZTouchRecord rec : this.touchRecords_) {
			c1 = c1.plus(new Vector2f(rec.downX_, rec.downY_));
			c2 = c2.plus(new Vector2f(rec.x_, rec.y_));
		}
		c1 = c1.divide(this.touchRecords_.size());
		c2 = c2.divide(this.touchRecords_.size());
		
		// compute average distance from center
		// this is used for normalizing the transformation
		float d1 = 0.f;
		float d2 = 0.f;
		for (ZTouchRecord rec:this.touchRecords_) {
			d1 += (new Vector2f(rec.downX_, rec.downY_).minus(c1).length());
			d2 += (new Vector2f(rec.x_, rec.y_).minus(c2).length());
		}
		d1 /= this.touchRecords_.size();
		d2 /= this.touchRecords_.size();
		
		if (touchRecords_.size()<2) {
			this.translationScore_ = 1.f;
			return;
		}
		
		// compute transformation (without translation)
		// only orthogonal part is extracted
		Matrix2f AAT = new Matrix2f();
		Matrix2f BAT = new Matrix2f();
		for (ZTouchRecord rec:this.touchRecords_) {
			Vector2f a = new Vector2f(rec.downX_, rec.downY_).minus(c1);
			Vector2f b = new Vector2f(rec.x_, rec.y_).minus(c2);
			AAT.e_[0] += a.x_*a.x_;
			AAT.e_[1] += a.x_*a.y_;
			AAT.e_[2] += a.y_*a.x_;
			AAT.e_[3] += a.y_*a.y_;
			BAT.e_[0] += b.x_*a.x_;
			BAT.e_[1] += b.x_*a.y_;
			BAT.e_[2] += b.y_*a.x_;
			BAT.e_[3] += b.y_*a.y_;
		}
		// add extra virtual points if only 2 touch points
		// to make sure tranformation matrix is solvable
		if (this.touchRecords_.size()==2) {
			for (ZTouchRecord rec : this.touchRecords_) {
				Vector2f a = new Vector2f(rec.downY_-c1.y_, c1.x_-rec.downX_);
				Vector2f b = new Vector2f(rec.y_-c2.y_, c2.x_-rec.x_);
				AAT.e_[0] += a.x_*a.x_;
				AAT.e_[1] += a.x_*a.y_;
				AAT.e_[2] += a.y_*a.x_;
				AAT.e_[3] += a.y_*a.y_;
				BAT.e_[0] += b.x_*a.x_;
				BAT.e_[1] += b.x_*a.y_;
				BAT.e_[2] += b.y_*a.x_;
				BAT.e_[3] += b.y_*a.y_;
			}
		}
		// compute the matrix and extract orthogonal part (scaling & rotation)
		Matrix2f T;
		try {
			T = BAT.multi(AAT.inverse()).orthogonalFactor(0.001f);
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		// normalization
		T = T.times(1.f/(T.e_[0]*T.e_[0]+T.e_[1]*T.e_[1]));
		
		// compute score for translation, sclaing and rotating
		this.transformation_ = T;
		this.translationScore_ = (c2.minus(c1).length())/d1;
		this.scalingRatio_ = (d2-d1)/d1;
		this.scalingScore_ = (float)Math.abs(scalingRatio_);
		float angle = (float)Math.acos(T.e_[0]);
		this.rotationScore_ = angle;
		
		Log.d(LOG_TAG, "" + translationScore_+ " " + scalingScore_ +" " + rotationScore_);
		// determin transformation mode
		if (this.transformationMode_ == TouchTransformationMode.Unknown) {
			if (translationScore_ > 0.3f &&
				translationScore_ > scalingScore_ &&
				translationScore_ > rotationScore_) {
				this.transformationMode_ = TouchTransformationMode.Translation;
			}
			else if (rotationScore_ > 0.3f &&
					 rotationScore_ >  scalingScore_) {
				this.transformationMode_ = TouchTransformationMode.Rotation;
			}
			else if (scalingScore_ > 0.2) {
				this.transformationMode_ = TouchTransformationMode.Scaling;
			}
		}
	}
	
	public ZTouchRecord[] getTouchRecordsByOrder() {
		if (this.touchRecords_.isEmpty()) return null;
		ZTouchRecord[] recs = new ZTouchRecord[this.touchRecords_.size()];
		touchRecords_.toArray(recs);
		return recs;
	}
	
	public String currentStatus() {
		String str = "";
		str += " gMode:" + this.transformationMode_;
		str += " " + this.translationScore_;
		str += " " + this.scalingScore_;
		str += " " + this.rotationScore_;
		return str;
	}
	
	public String currentStatusTouchRecords() {
		String str = "";
		int idx = 0;
		for (ZTouchRecord g : this.touchRecords_) {
			str += idx + ":" + g.currentStatus();
			idx++;
		}
		return str;
	}
}

