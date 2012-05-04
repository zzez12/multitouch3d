package zhl.Android.Multitouch.touch;

import java.util.HashSet;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;
import android.util.Log;

import zhl.Android.Multitouch.render.ZView;
import zhl.Android.Multitouch.render.ZView.EnumOperationMode;
import zhl.Android.Multitouch.touch.ZFingerRegistration.EnumPalm;
import zhl.Android.Multitouch.touch.ZFingerRegistration.TouchTransformationMode;
import zhl.Android.math.Matrix2f;
import zhl.Android.math.Matrix3f;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Trackball;
import zhl.Android.math.Vector2f;
import zhl.Android.math.Vector3f;
import zhl.Android.scenes.ZAxis;
import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZLine3D;
import zhl.Android.scenes.ZMesh;
import zhl.Android.scenes.ZObject3D;
import zhl.Android.scenes.ZAxis.AxisType;

public class ZFingerRegisterListener implements ZFingerRegister.FingerRegisterListener {

	public static final String LOG_TAG = ZFingerRegisterListener.class.getSimpleName();
	
	private EnumOperationMode operationMode_ = EnumOperationMode.Unknown;
	private ZObject3D downObject_ = null;
	private ZObject3D focusObject_ = null;
	private ZObject3D touchObject_ = null;
	private ZObject3D centerObject_ = null;
	private ZTouchRecord globalRotationTouchRecord_ = null;
	private boolean boundObject_ = false;
	
	private ZView view_;
	//private Matrix4f currTouchTransformation_ = Matrix4f.identityMatrix();
	private HashSet<ZObject3D> selectedObjects_ = new HashSet<ZObject3D>();
	
	private boolean useSupportOpertaions_ = false;
	private boolean useReferenceObjectCenter_ = false;

	private int axisHoldThreshold_ = 12;
	private int axisHoldCount_ = 0;
	private long lastTwoTapDownTime_ = -1;
	
	private ZObject3D referenceObject_ = null;

	private boolean useGlobalAxes_;

	private boolean useScreenAxes_;
	
	private ZLine3D tmpAxisLine = new ZLine3D();
	private ZLine3D tmpAxisLine3D = new ZLine3D();
	
	public ZFingerRegisterListener(ZView view) {
		this.view_ = view;
	}
	
	public void onGroupCreate(Object sender, ZTouchGroup group) {	
	}

	public void onGroupRemove(Object sender, ZTouchGroup group) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupRemove()"  + currentStatus());
		
		/// global browsing
		if (operationMode_ == EnumOperationMode.GlobalPan
			|| operationMode_ == EnumOperationMode.GlobalRotation
			|| operationMode_ == EnumOperationMode.GlobalZoom) {
			// apply global transformation
			Matrix4f m = view_.getTrackball().getMatrix();
			//this.currTouchTransformation_ = m.multiply(currTouchTransformation_);
			
			// update status
			view_.endTransformation();
			view_.getTrackball().end();
			this.operationMode_ = EnumOperationMode.Unknown;
			
			// update axes
			if (this.focusObject_ != null) {
				resetAxis(this.focusObject_);
			}
		}
		
		/// axis/planner transformation
		if (operationMode_ == EnumOperationMode.Translation
			|| operationMode_ == EnumOperationMode.Rotation
			|| operationMode_ == EnumOperationMode.Scaling
			|| operationMode_ == EnumOperationMode.UniformScale
			|| operationMode_ == EnumOperationMode.PlannerScaling
			|| operationMode_ == EnumOperationMode.PlannerTranslation) {
			// update axes
			for (ZObject3D obj : this.selectedObjects_) {
				//obj.applyTransformation(currTouchTransformation_);
				obj.endTransformation();
				resetAxis(this.focusObject_);
			}
			
			// update status
			this.focusObject_.setSelectedAxis(null);
			this.focusObject_.setSelectedPlaneAxes(null);
			//this.currTouchTransformation_ = Matrix4f.identityMatrix();
			this.operationMode_ = EnumOperationMode.Unknown;
			
		}
		
		/// active snapping
		if (operationMode_ == EnumOperationMode.ActiveSnap) {
			// snap object if source and target snap planes are defined
			if (this.focusObject_ != null
				&& this.touchObject_ != null
				&& this.focusObject_ != touchObject_
				&& this.focusObject_.getSelectedSnapPlane() != null
				&& this.touchObject_.getSelectedSnapPlane() != null) {
				//snapObject(focusObject_.getSelectedSnapPlane(), touchObject_.getSelectedSnapPlane());
			}
		}
		
		// deselect axis and plane
		if (focusObject_ != null) {
			this.focusObject_.setSelectedAxis(null);
			this.focusObject_.setSelectedPlaneAxes(null);
			this.focusObject_.setFinishedAxisSelection(false);
			this.axisHoldCount_ = 0;
		}
	}

	public void onGroupMove(Object sender, ZTouchGroup group,
			ZTouchRecord record) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onGroupMove()"  + currentStatus());
		//Log.d(LOG_TAG, " group: " + group.currentStatus());
		//if (record!=null) Log.d(LOG_TAG, " record:" + record.currentStatus());
		
		/// start global rotation
		if (operationMode_ == EnumOperationMode.Unknown
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()==1
			&& downObject_ == null
			&& record != null
			&& record.moved_) {
			float x = record.x_;
			float y = view_.getHeight() - record.y_;
			Vector2f p = new Vector2f(x, y);
			view_.getTrackball().click(p, Trackball.MotionType.Rotation);
			this.operationMode_ = EnumOperationMode.GlobalRotation;
			this.globalRotationTouchRecord_ = record;
			
			// hide axes
			if (this.focusObject_ != null) {
				focusObject_.hideAxes();
			}
			
			return;
		}
		
		/// global browsing
		// +global rotation
		if (operationMode_ == EnumOperationMode.GlobalRotation
			&& record != null
			&& record == globalRotationTouchRecord_) {
			Vector2f p = new Vector2f(record.x_, view_.getHeight() - record.y_);
			view_.getTrackball().drag(p);
			return;
		}
		// +global panning
		if (operationMode_ == EnumOperationMode.GlobalPan
			&& group.oldTouchRecords_.size()==0) {
			Vector2f p = new Vector2f(group.cx_, view_.getHeight()-group.cy_);
			view_.getTrackball().drag(p.times(3.f/view_.getViewportScaleRatio()));
			return;
		}
		// +global zooming
		if (operationMode_ == EnumOperationMode.GlobalZoom
			&& group.oldTouchRecords_.size()==0) {
			Vector2f p = new Vector2f(group.scalingRatio_, 0.f).times(view_.getWidth());
			view_.getTrackball().drag(p); 
			return;
		}
		// +axis/planner transformation
		// compute centers
		Vector2f c1 = new Vector2f();
		Vector2f c2 = new Vector2f();
		for (ZTouchRecord rec : group.touchRecords_) {
			c1 = c1.plus(new Vector2f(rec.downX_, rec.downY_));
			c2 = c2.plus(new Vector2f(rec.x_, rec.y_));
		}
		c1 = c1.divide(group.touchRecords_.size());
		c2 = c2.divide(group.touchRecords_.size());
		// compute average distance to centers
		float d1 = 0.f;
		float d2 = 0.f;
		for (ZTouchRecord rec : group.touchRecords_) {
			d1 += new Vector2f(rec.downX_, rec.downY_).minus(c1).length();
			d2 += new Vector2f(rec.x_, rec.y_).minus(c2).length();
		}
		d1 /= group.touchRecords_.size();
		d2 /= group.touchRecords_.size();
		
		// planner translation
		if (operationMode_ == EnumOperationMode.PlannerTranslation
			&& group.oldTouchRecords_.size()==0) {
			// get translation scale
			// adjust with buffer zoom
			float scale = group.translationScore_;
			float threshold = 0.3f;
			if (scale>threshold) {
				scale = scale - threshold;
			} else if (scale<-threshold) {
				scale = scale + threshold;
			} else {
				scale = 0.f;
			}
			// compute ratio (to align with 2D movement)
			ZAxis axis1 = focusObject_.getSelectedPlaneAxes()[0];
			ZAxis axis2 = focusObject_.getSelectedPlaneAxes()[1];
			Vector2f c21 = new Vector2f(c2.x_-c1.x_, c1.y_-c2.y_);
			Vector2f projAxis1 = axis1.getProjectedAxis();
			Vector2f projAxis2 = axis2.getProjectedAxis();
			float ratio1 = axis1.getProjectionScale();
			float ratio2 = axis2.getProjectionScale();
			float s1;
			float s2;
			Matrix2f A = new Matrix2f(projAxis1, projAxis2);
			if (A.determinate()!=0) {
				Vector2f q = A.inverse().multi(c21);
				s1 = q.x_;
				s2 = q.y_;
			} else {
				s1 = c21.dot(projAxis1) / projAxis1.length() / ratio1;
				s2 = c21.dot(projAxis2) / projAxis2.length() / ratio2;
			}
			Vector3f t = axis1.getCurrentDir(false).times(s1).plus(axis2.getCurrentDir(false).times(s2));
			
			// get translate matrix
			Matrix4f tran = Matrix4f.translationMatrix(t);
			focusObject_.setTmpTransformation(tran); 
			
//			if (boundObject_ && !checkTransformation(tran)) {
//				return;
//			}
			//this.currTouchTransformation_  = tran;
			return;
		}
		
		/// planner scaling
		if (operationMode_ == EnumOperationMode.PlannerScaling
			&& group.oldTouchRecords_.size()==0) {
			// get scaling scale
			// adjust with buffer zoom
			float scale = group.scalingRatio_;
			float threshold = 0.3f;
			if (scale>threshold) {
				scale = scale - threshold;
			} else if (scale<-threshold) {
				scale = scale + threshold;
			} else {
				scale = 0.f;
			}
			// compute scaling matrix
			ZAxis axis1 = focusObject_.getSelectedPlaneAxes()[0];
			ZAxis axis2 = focusObject_.getSelectedPlaneAxes()[1];
			Vector3f a1 = axis1.getDir().normalize();
			Vector3f a2 = axis2.getDir().normalize();
			Matrix3f s1 = a1.outerCross(a1).times(scale);
			Matrix3f s2 = a2.outerCross(a2).times(scale);
			s1.set(0, 0, s1.get(0, 0)+1);
			s1.set(1, 1, s1.get(1, 1)+1);
			s1.set(2, 2, s1.get(2, 2)+1);
			s2.set(0, 0, s2.get(0, 0)+1);
			s2.set(1, 1, s2.get(1, 1)+1);
			s2.set(2, 2, s2.get(2, 2)+1);
			Matrix4f T1 = new Matrix4f(s1);
			Matrix4f T2 = new Matrix4f(s2);
			T1.set(3, 3, 1.f);
			T2.set(3, 3, 1.f);
			
			// get rotation matrix
			Vector3f c = focusObject_.getObjCenter();
			Matrix4f tran = Matrix4f.translationMatrix(c);
			tran = tran.multiply(T1).multiply(T2).multiply(Matrix4f.translationMatrix(c.times(-1.f)));
			this.focusObject_.setTmpTransformation(tran);
			// TODO
			
//			if (boundObject_ && !checkTransformation(tran)) {
//				return;
//			}
			//this.currTouchTransformation_ = tran;
			return;
		}
		
		/// axis translation
		if (operationMode_ == EnumOperationMode.Translation
			&& focusObject_ != null && focusObject_.getSelectedAxis()!=null
			&& group.oldTouchRecords_.size()==0) {
			ZAxis axis = focusObject_.getSelectedAxis();
			Vector2f projAxis = axis.getProjectedAxis();
			c1 = new Vector2f(group.touchRecords_.first().downX_, group.touchRecords_.first().downY_);
			c2 = new Vector2f(group.touchRecords_.first().x_, group.touchRecords_.first().y_);
			
			Vector2f c = new Vector2f(c2.x_ - c1.x_, c1.y_ - c2.y_);
			float scale = c.dot(projAxis.normalize()) / axis.getProjectionScale();
			
			if (scale>0.3f) scale -= 0.3f;
			else if (scale<-0.3f) scale += 0.3f;
			else scale = 0.f;
			
			Vector3f t = axis.getCurrentDir(false).plus(axis.getCurrentDir(false).normalize()).times(scale);
			//Vector3f t = axis.getCurrentDir(false).times(scale);
			
			// get translate matrix
			//  UNDO:
			
			Matrix4f tran = Matrix4f.translationMatrix(t);
			focusObject_.setTmpTransformation(tran);
			//this.currTouchTransformation_ = tran;
			return;
		}
		
		/// axis rotation
		if (operationMode_ == EnumOperationMode.Rotation
			&& group.oldTouchRecords_.size()==0) {
			ZAxis axis = focusObject_.getSelectedAxis();
			Vector2f projAxis = axis.getProjectedAxis();
			Vector2f p = new Vector2f(c2.y_-c1.y_, c2.x_-c1.x_);
			float scale = p.dot(projAxis.normalize()) / axis.getProjectionScale();
			
			if (scale>0.3f) {
				scale -= 0.3f;
			} else if (scale<-0.3f) {
				scale += 0.3f;
			} else {
				scale = 0.f;
			}
			
			// get rotation matrix
			Vector3f c = axis.getCurrentOri(false); 
			Vector3f a = axis.getCurrentDir(false);
			//Log.d(LOG_TAG, "center:" + c + " dir:" + a);
			
			// TODO
			Matrix4f tran = Matrix4f.rotationMatrix(c, a, (float)(scale*180.f/Math.PI));
			this.focusObject_.setTmpTransformation(tran);
			//this.currTouchTransformation_ = tran;
			return;
		}
		
		/// axis scaling
		if (operationMode_ == EnumOperationMode.Scaling
			&& group.oldTouchRecords_.size() ==0) {
			// get scaling scale
			// adjust with buffer zoom
			float scale = group.scalingRatio_;
			float threshold = 0.3f;
			if (scale>threshold) {
				scale = scale - threshold;
			} else if (scale<-threshold) {
				scale = scale + threshold;
			} else {
				scale = 0.f;
			}
			
			// computing scalig matrix
			ZAxis axis = focusObject_.getSelectedAxis();
			Vector3f a = axis.getCurrentDir(false).normalize();
			Matrix3f S = a.outerCross(a).times(scale);
			S.set(0, 0, S.get(0,0)+1);
			S.set(1, 1, S.get(1,1)+1);
			S.set(2, 2, S.get(2,2)+1);
			Matrix4f S2 = new Matrix4f(S);
			S2.set(3, 3, 1.f);
			
			// get rotation matrix
			// TODO
			Vector3f c = this.focusObject_.getObjCenter();		
			Matrix4f tran = Matrix4f.translationMatrix(c);
			tran = tran.multiply(S2).multiply(Matrix4f.translationMatrix(c.times(-1.f)));
			this.focusObject_.setTmpTransformation(tran);
			//this.currTouchTransformation_ = tran;
			return;
		}
		
		/// object uniform scaling
		if (operationMode_ == EnumOperationMode.UniformScale
			&& group.oldTouchRecords_.size()==0) {
			// get scaling scale
			// adjust with buffer zoom
			float scale = group.scalingRatio_;
			float threshold = 0.3f;
			if (scale > threshold) scale = scale-threshold;
			else if (scale < -threshold) scale = scale + threshold;
			else scale = 0.f;
			
			// compute scaling matrix
			Matrix4f S = Matrix4f.identityMatrix().times(1.f+scale);
			S.set(3, 3, 1.f);
			
			// get matrix
			Vector3f c = focusObject_.getObjCenter();
			Matrix4f tran = Matrix4f.scalingMatrix(c, 1.f+scale);
			focusObject_.setTmpTransformation(tran);
			// get matrix
		}
	}

	public void onGroupLongMove(Object sender, ZTouchGroup group,
			ZTouchRecord record) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupLongMove()"  + currentStatus());
		//Log.d(LOG_TAG, " group: " + group.currentStatus());
		//if (record!=null) Log.d(LOG_TAG, " record:" + record.currentStatus());
		
		// update touch object if in ActiveSnap mode
		if (operationMode_ == EnumOperationMode.ActiveSnap
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()==1) {
			float x = record.x_;
			float y = view_.getHeight() - record.y_;
			if (touchObject_ != null && touchObject_!=downObject_) {
				touchObject_.deselectAllSnapPlanes();
			}
			// ???pick object WITHOUT update depth buffer (for fast computation)
			this.touchObject_ = pickObject3D(x, y);
		}
		
		// Unknown mode and 1 point moving on focused object -> start active snapping
		if (operationMode_ == EnumOperationMode.Unknown
			&& this.useSupportOpertaions_ == true
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()==1
			&& focusObject_ != null
			&& focusObject_ == downObject_ 
			&& record.prevListX_.size()>=2) {
			// TODO
		}
		
		// ActiveSnap mode and 1 point moving on any object -> active snapping (pick snap plane)
		if (operationMode_ == EnumOperationMode.ActiveSnap
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size() == 1
			&& touchObject_ != null
			&& record.prevListX_.size() >= 2) {
			// TODO
		}
		
		// two point contact -> pick axis / pick plane
		if (operationMode_ == EnumOperationMode.Unknown
			&& focusObject_ != null 
			&& axisHoldCount_ < axisHoldThreshold_
			&& group.oldTouchRecords_.size() ==0
			&& group.touchRecords_.size() == 2) {
			// if previous two-point tap found => select plane
			// else select axis
			Log.d(LOG_TAG, "Long-Time:" + group.touchRecords_.first().downTime_ + "-" + lastTwoTapDownTime_);
			if (group.touchRecords_.first().downTime_ - lastTwoTapDownTime_ < 1000) {
				ZAxis axis = pickAxis(group);
				focusObject_.setSelectedAxis(axis);
				ZAxis[] planeAxes = pickPlane(group);
				focusObject_.setSelectedPlaneAxes(planeAxes);
			}
			else {
				ZAxis axis = pickAxis(group);
				focusObject_.setSelectedAxis(axis);
				focusObject_.setSelectedPlaneAxes(null);
			}
			
			ZTouchRecord r1 = group.touchRecords_.first();
			ZTouchRecord r2 = group.touchRecords_.last();
			r1.clear();
			r2.clear();
			
			group.transformationMode_ = TouchTransformationMode.Unknown;
			group.moved_ = false;
			
			// adjust axis hold time threshold
			if (focusObject_.getSelectedAxis() != null) {
//				float s = findClosestAxisAngle(focusObject_.getSelectedAxis());
//				s = 1.f - (float)(Math.acos(s)/(Math.PI/2.f));
//				if (Float.isNaN(s) || s<0 || s>1) s = 1;
//				s = s*s;
//				this.axisHoldThreshold_ = (int)(2+s*0);
				this.axisHoldThreshold_ = 2;
			}
			this.axisHoldCount_ = 0; 
		}
	}

	public void onGroupStartTransform(Object sender, ZTouchGroup group) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onGroupStartTransform()"  + currentStatus());
		Log.d(LOG_TAG, " group: " + group.currentStatus());
		
		/// start global panning
		if (operationMode_ == EnumOperationMode.Unknown
			&& useSupportOpertaions_ == true
			&& group.transformationMode_ == TouchTransformationMode.Translation
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size() >= 4) {
			Vector2f p = new Vector2f(group.cx_, view_.getHeight() - group.cy_);
			view_.getTrackball().click(p.times(3.f/view_.getViewportScaleRatio()), Trackball.MotionType.Pan);
			operationMode_ = EnumOperationMode.GlobalPan;
			
			// hide axes
			if (this.focusObject_ != null) {
				focusObject_.hideAxes();
			}
			return;
		}
		
		/// start global zooming
		if (operationMode_ == EnumOperationMode.Unknown
			//&& useSupportOpertaions_ == true
			&& group.transformationMode_ == TouchTransformationMode.Scaling
			&& group.oldTouchRecords_.size()==0 //) {
			&& group.touchRecords_.size()>=4  
			&& group.palm_ == EnumPalm.Right) {
			Vector2f p = new Vector2f(group.scalingRatio_, 0.f);
			p = p.times(view_.getWidth());
			view_.getTrackball().click(p, Trackball.MotionType.Scale);
			operationMode_ = EnumOperationMode.GlobalZoom;
			
			// hide axes
			if (focusObject_ != null) {
				focusObject_.hideAxes();
			}
			return;
		}
		
		/// start uniform zooming
		if (operationMode_ == EnumOperationMode.Unknown
			&& useSupportOpertaions_ == true
			&& group.transformationMode_ == TouchTransformationMode.Scaling
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()>=4
			&& group.palm_ == EnumPalm.Left) {
			operationMode_ = EnumOperationMode.UniformScale;
		}
		
		/// start axis translation / axis rotation
		if (operationMode_ == EnumOperationMode.Unknown
			//&& useSinglePointUI == false 
			&& focusObject_ != null
			&& focusObject_.getSelectedAxis()!=null
			&& focusObject_.getSelectedPlaneAxes()==null
			&& group.transformationMode_ == TouchTransformationMode.Translation
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size() == 2) {
			// compute movement centers
			Vector2f c1 = new Vector2f();
			Vector2f c2 = new Vector2f();
			for (ZTouchRecord rec : group.touchRecords_) {
				c1 = c1.plus(new Vector2f(rec.downX_, rec.downY_));
				c2 = c2.plus(new Vector2f(rec.x_, rec.y_));
			}
			c1 = c1.divide(group.touchRecords_.size());
			c2 = c2.divide(group.touchRecords_.size());
			
			// determine operation mode (axis translation or axis rotation)
			 ZAxis axis = focusObject_.getSelectedAxis();
			 Vector2f proj_axis = axis.getProjectedAxis().normalize();
			 Vector2f movement = c2.minus(c1).normalize();
			 movement.y_ = -movement.y_;
			 float dot = Math.abs(proj_axis.dot(movement));
			 
			 if (dot>0.7071) {
				 this.operationMode_ = EnumOperationMode.Translation;
			 }
			 else {
				 this.operationMode_ = EnumOperationMode.Rotation;
			 }
			 this.focusObject_.setFinishedAxisSelection(true);
		}
		
		/// start axis scaling
		if (operationMode_ == EnumOperationMode.Unknown
			//&& useSinglePointUI == false
			&& focusObject_ != null
			&& focusObject_.getSelectedAxis()!=null
			&& focusObject_.getSelectedPlaneAxes()==null		// only axis is selected
			&& focusObject_.getSelectedAxis().getType() == ZAxis.AxisType.ObjectAxis	// only object axis can be used for scaling
			&& group.transformationMode_ == TouchTransformationMode.Scaling
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size()==2) {
			this.focusObject_.setFinishedAxisSelection(true);
			this.operationMode_ = EnumOperationMode.Scaling;
		}
		
		/// start planner translation
		if (operationMode_ == EnumOperationMode.Unknown
			//&& useSinglePointUI == false
			&& focusObject_ != null
			&& focusObject_.getSelectedPlaneAxes()!=null
			&& group.transformationMode_ == TouchTransformationMode.Translation
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size()==2) {
			this.focusObject_.setFinishedAxisSelection(true);
			this.operationMode_ = EnumOperationMode.PlannerTranslation;
		}
		
		/// start planner scaling
		if (operationMode_ == EnumOperationMode.Unknown
			//&& useSinglePointUI == false
			&& focusObject_ != null
			&& focusObject_.getSelectedPlaneAxes()!=null
			&& focusObject_.getSelectedAxis().getType() == ZAxis.AxisType.ObjectAxis
			&& group.transformationMode_ == TouchTransformationMode.Scaling
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size()==2) {
			this.focusObject_.setFinishedAxisSelection(true);
			this.operationMode_ = EnumOperationMode.PlannerScaling;
		}
		
		/// start copying object
		// TODO
		Log.d(LOG_TAG, "~onGroupStartTransform()"  + currentStatus());
	}

	public void onGroupTap(Object sender, ZTouchGroup group) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupTap()"  + currentStatus());
		
		float x = group.cx_;
		float y = view_.getHeight() - group.cy_;
		
		/// pick and hide 2D object under tap point
		if (group.oldTouchRecords_.size()==1) {
			// TODO
		}
		
		/// one-point tap - pick 3D object
		if (group.oldTouchRecords_.size()==1) {
			ZObject3D obj = pickObject3D(x, y);
			if (obj!=null) Log.d(LOG_TAG, obj.getName());
			
			// first deseect current focused object
			if (this.focusObject_ != null) {
				this.focusObject_.setFocused(false);
				this.focusObject_.setSelected(false);
				this.selectedObjects_.remove(focusObject_);
				this.focusObject_.hideAxes();
				this.focusObject_ = null;
			}
			
			// ignore if newly picked object is reference object
			if (obj != null && obj == referenceObject_) {
				return;
			}
			
			// set new focused object
			//Log.d(LOG_TAG, ""+obj);
			if (obj instanceof ZMesh) {
				ZMesh m = (ZMesh)obj;
				this.focusObject_ = m;
				this.focusObject_.setFocused(true);
				this.focusObject_.setSelected(true);
				this.selectedObjects_.add(m);
				resetAxis(this.focusObject_);
			}
		}
		
		/// 2-point tap - save tap time (for planner transformations)
		if (group.oldTouchRecords_.size()==2
			//&& this.useSupportOpertaions_ == true
			&& this.operationMode_ == EnumOperationMode.Unknown
			&& focusObject_ != null) {
			this.lastTwoTapDownTime_ = group.oldTouchRecords_.get(0).downTime_;
			
			ZTouchRecord rec1 = group.oldTouchRecords_.get(0);
			ZTouchRecord rec2 = group.oldTouchRecords_.get(group.oldTouchRecords_.size()-1);
			float x1 = rec1.x_;
			float x2 = rec2.x_;
			float y1 = view_.getHeight() - rec1.y_;
			float y2 = view_.getHeight() - rec2.y_;
			
			ZObject3D o1 = pickObject3D(x1, y1);
			ZObject3D o2 = pickObject3D(x2, y2);
			if (o1 instanceof ZMesh && o2 instanceof ZMesh) {
				ZMesh m1 = (ZMesh)o1;
				ZMesh m2 = (ZMesh)o2;
				if (m1!=m2) return;
				if (m1==null) m1 = m2;
				if (m1==focusObject_) return;
				
				if (m1.isSelected()) {
					m1.setSelected(false);
					this.selectedObjects_.remove(m1);
				}
				else {
					m1.setSelected(true);
					this.selectedObjects_.add(m1);
				}
			}
		}
		
		
		/// 4/5 point tap -select 3D object under index finger
		// UN-supported
		
		//Log.d(LOG_TAG, "onGroupTap()"  + currentStatus());
	}

	public void onGroupDoubleTap(Object sender, ZTouchGroup group) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupDoubleTap()"  + currentStatus());
		
		// single tap - pick 3d object
		if (group.oldTouchRecords_.size()==1) {
			float x = group.cx_;
			float y = view_.getHeight() - group.cy_;
			ZObject3D obj = pickObject3D(x, y);
			
			if (this.focusObject_ != null) {
				this.focusObject_.setFocused(false);
				this.focusObject_ = null;
			}
			
			if (obj != null && obj instanceof ZMesh) {
				this.focusObject_ = (ZMesh)obj;
				this.focusObject_.setFocused(true);
				this.centerObject_ = (ZMesh)obj;
			}
		}
	}

	public void onGroupHold(Object sender, ZTouchGroup group) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupHold()"  + currentStatus());
		
		// pick reference object
		if (operationMode_ == EnumOperationMode.Unknown
			//&& this.useSinglePointUI == false
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()==1) {
			float x = group.cx_;
			float y = view_.getHeight() - group.cy_;
			ZObject3D obj = pickObject3D(x, y);
			
			if (obj == null) {
				this.referenceObject_ = null;
			}
			else if (obj==this.referenceObject_) {
				this.useReferenceObjectCenter_ = !this.useReferenceObjectCenter_;
			}
			else if (obj instanceof ZMesh) {
				this.referenceObject_ = obj;
				this.useReferenceObjectCenter_ = false;
			}
			
			// ensure reference Object is not the focused object
			if (this.referenceObject_ != null && this.referenceObject_ == this.focusObject_) {
				this.focusObject_.setFocused(false);
				this.focusObject_.hideAxes();
				this.focusObject_ = null;
			}
			
			if (this.focusObject_ != null) {
				this.focusObject_.hideAxes();
				resetAxis(this.focusObject_);
			}
		}
	}

	public void onGroupAddPoint(Object sender, ZTouchGroup group,
			ZTouchRecord record) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupAddPoint()" + currentStatus());
		//Log.d(LOG_TAG, " group: " + group.currentStatus());
		//Log.d(LOG_TAG, " record:" + record.currentStatus());
		
		/// one point contact -> find down object
		if (operationMode_ == EnumOperationMode.Unknown
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size() == 1) {
			float x = record.x_;
			float y = view_.getHeight() - record.y_;
			this.downObject_ = pickObject3D(x, y);
			if (!(downObject_ instanceof ZMesh)) {
				this.downObject_ = null;
			}
			this.touchObject_ = downObject_;
		}
		
		/// single point UI - 1 point touch - pick axis
		// TODO
		
		/// single point UI - 2 point touch - check scaling
		// TODO
		
		/// two point contact -> pick axis / pick plane
		if (operationMode_ == EnumOperationMode.Unknown
			//&& this.useSinglePointUI == false
			&& this.focusObject_ != null
			&& group.oldTouchRecords_.size() == 0
			&& group.touchRecords_.size() == 2) {
			// clear selected axis/plane
			focusObject_.setSelectedAxis(null);
			focusObject_.setSelectedPlaneAxes(null);
			
			// if previous two-point tap found => select plane
			// else select axis
			Log.d(LOG_TAG, "Add-Time:" + group.touchRecords_.first().downTime_ + "-" + lastTwoTapDownTime_ + "=" + (record.downTime_ - this.lastTwoTapDownTime_));
			if (record.downTime_ - this.lastTwoTapDownTime_ < 2000) {
				ZAxis axis = pickAxis(group);
				focusObject_.setSelectedAxis(axis);
				ZAxis[] planeAxes = pickPlane(group);
				focusObject_.setSelectedPlaneAxes(planeAxes);
			}
			else {
				ZAxis axis = pickAxis(group);
				focusObject_.setSelectedAxis(axis);
			}
			
			// adjust axis hold time threshold
			if (focusObject_.getSelectedAxis() != null) {
//				float s = findClosestAxisAngle(focusObject_.getSelectedAxis());
//				s = 1.f - (float)(Math.acos(s) / (Math.PI/2));
//				if (Float.isNaN(s) || s<0 || s>1) s=1.f;
//				s = s*s;
//				this.axisHoldThreshold_ = (int)(2+s*0);
				this.axisHoldThreshold_ = 2;
			}
			
			this.axisHoldCount_ = 0;
		}
		
		/// three point contact -> cancel axis / panel selection
		if (operationMode_ == EnumOperationMode.Unknown
			//&& this.useSingerPointUI == false
			&& focusObject_ != null
			&& group.oldTouchRecords_.size()==0
			&& group.touchRecords_.size()==3) {
			// clear selected axis / plane
			focusObject_.setSelectedAxis(null);
			focusObject_.setSelectedPlaneAxes(null);
		}
		//Log.d(LOG_TAG, "onGroupAddPoint()" + currentStatus());
		//Log.d(LOG_TAG, " group: " + group.currentStatus());
	}

	public void onGroupRemovePoint(Object sender, ZTouchGroup group,
			ZTouchRecord record) {
		// TODO Auto-generated method stub
		//Log.d(LOG_TAG, "onGroupRemovePoint()");
	}
	
	private boolean checkTransformation(Matrix4f tran) {
		float f1 = tran.get(1);
		float f2 = tran.get(5);
		float f3 = tran.get(9);
		float f4 = tran.get(13);
		//TODO
		
		for (ZObject3D obj : this.selectedObjects_) {
			if (obj instanceof ZMesh) {
				ZMesh mesh = (ZMesh)obj;
				//for (int i=0; i<mesh.g\)
			}
		}
		return false;
	}

	private void resetAxis(ZObject3D obj) {
		obj.removeNonObjectAxes();
		if (useGlobalAxes_) obj.addGlobalAxes();
		if (useScreenAxes_) obj.addScreenAxes(view_.getProjector());
		if (referenceObject_ != null) {
			obj.addReferenceAxes(referenceObject_, false);
		}
		obj.showAxes();
	}
	
	private ZObject3D pickObject3D(float x, float y) {
		// check point within viewport
		RectF viewport = new RectF(0, 0, view_.getWidth(), view_.getHeight());
		if (viewport.contains(x, y)==false) return null;
		
//		ZLine3D line = view_.getProjector().getRayLineFromTouchPoint(x, y);
//		ZDataManager.getDataManager().getAllObject3D().add(line);
//		view_.updateRenderData();
//		Log.d(LOG_TAG, ZDataManager.getDataManager().getAllObject3D().size() + " " + line.toString());
		// update projector
		// iterative check all 3D objects
		ZObject3D pickedObj = null;
		float minDis = Float.MAX_VALUE;
		for (ZObject3D obj : ZDataManager.getDataManager().getAllObject3D()) {
			if (obj instanceof ZObject3D) {
				ZObject3D obj3d = (ZObject3D)obj;
				if (obj3d.isVisable() && obj3d.pick(view_.getProjector(), x, y)) {
					float dis = (Float)obj3d.getPickedObjs().get(0);
					if (dis<minDis) {
						pickedObj = obj;
						minDis = dis;
					}
				}
			}
		}
		return pickedObj;
	}
	
	private ZAxis pickAxis(ZTouchGroup group) {
		// NOTE: Not tested!!
		ZTouchRecord[] records = group.getTouchRecordsByOrder();//(ZTouchRecord[]) group.touchRecords_.toArray();
		float x1 = records[0].x_;
		float x2 = records[1].x_;
		float y1 = view_.getHeight() - records[0].y_;
		float y2 = view_.getHeight() - records[1].y_;
		Vector2f touchOrientation = new Vector2f(x2-x1, y2-y1).normalize();
		return pickAxis(touchOrientation);
	}
	
	private ZAxis pickAxis(Vector2f touchOrientation) {
		// some checkings
		if (focusObject_ == null) return null;
		touchOrientation = touchOrientation.normalize();
		
		// update projector matrices
		
		// find closest axis
		float maxError = Float.MIN_VALUE;
		ZAxis maxAxis = null;
		for (ZObject3D obj : focusObject_.getChildObjects()) {
			if (obj instanceof ZAxis) {
				ZAxis axis = (ZAxis)obj;
				if (this.referenceObject_ != null
					&& axis.getType() != ZAxis.AxisType.ReferenceAxis
					&& axis.getType() != ZAxis.AxisType.ReferencePivotAxis) 
					continue;
				
				// compute projected axis
				Vector3f c = axis.getCurrentOri(false);
				Vector3f v = c.plus(axis.getCurrentDir(false)).plus(axis.getCurrentDir(false).normalize());
				Vector3f proj_c = view_.getProjector().project(c);
				Vector3f proj_v = view_.getProjector().project(v);
				Vector2f projectedAxis2D = new Vector2f(proj_v.x_ - proj_c.x_, proj_v.y_ - proj_c.y_);
				
				
				// store projected coordinates to axis object
				axis.setProjectedAxis(projectedAxis2D);
				axis.setProjectionScale(projectedAxis2D.length());
				
				// skip axis with short length
				float lengthRatio = projectedAxis2D.length() / view_.getHeight();
				if (lengthRatio<0.05) continue;
				
				// compute error
				float error = Math.abs(projectedAxis2D.normalize().dot(touchOrientation));
				// adjust error value (lower the value if is not object axis)
				if (axis.getType() != AxisType.ObjectAxis) {
					error = error*error;
				}
				// get the best one
				if (error>maxError) {
					maxError = error;
					maxAxis = axis;
					// store the projected axis to show
					this.tmpAxisLine.setLine(proj_c, proj_v);
					this.tmpAxisLine.setColor(1.f, 1.f, 0.f, 1.f);
					this.tmpAxisLine3D.setLine(c, v);
					this.tmpAxisLine3D.setColor(0.f, 1.f, 1.f, 1.f);
				}
			}
		}
		//Log.d(LOG_TAG, " Axis:" + maxAxis);
		return maxAxis;
	}

	// return the axes that define a plane
	private ZAxis[] pickPlane(ZTouchGroup group) {
		Log.d(LOG_TAG, "Begin to pick plane..");
		// first pick the axis
		ZAxis pickedAxis = pickAxis(group);
		if (pickedAxis==null) return null;
		if (pickedAxis.getType()==ZAxis.AxisType.ScreenAxis) return null; // disable planer transformation for screen axes
		
		// find the other axes in the same frame
		// to define the orthogonal plane
		int i=0; 
		ZAxis[] orthogonalAxes = new ZAxis[2];
		for (ZObject3D obj:focusObject_.getChildObjects()) {
			if (obj instanceof ZAxis) {
				ZAxis axis = (ZAxis)obj;
				if (axis == pickedAxis) continue;
				if (axis.getType() != pickedAxis.getType()) continue;
				
				orthogonalAxes[i++] = axis;
			}
		}
		
		// return found two axes
		if (i!=2) return null;
		return orthogonalAxes;
	}
	
	private String currentStatus() {
		String str = "";
		str += " Mode:" + this.operationMode_;
		str += " crtObj: " + this.focusObject_;
		str += " downObj: " + this.downObject_;
		return str;
	}

	public ZView getView() {
//		 TODO Auto-generated method stub
		return view_;
	}

	public void onDraw(GL10 gl) {
		gl.glPushMatrix();
		tmpAxisLine.draw(gl);
		tmpAxisLine3D.draw(gl);
		gl.glPopMatrix();
	}
}
