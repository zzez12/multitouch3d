package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector4f;

public class ZSnapPlane extends ZObject3D {
	
	private Vector3f center_ = new Vector3f();
	private Vector3f normal_ = new Vector3f();
	private Vector3f frame1_ = new Vector3f();
	private Vector3f frame2_ = new Vector3f();
	
	// constructor
	public ZSnapPlane(Vector3f center, Vector3f f1, Vector3f f2) {
		this.setPlaneCenter(center);
		this.setFrame1(f1);
		this.setFrame2(f2);
		this.setPlaneNormal(f2.cross(f1).normalize());
		
		this.setVisable(false);
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareBuffers() {
		// TODO Auto-generated method stub

	}
	
	

	@Override
	public void applyTransformation(Matrix4f tran) {
		// TODO Auto-generated method stub
		setPlaneCenter(new Vector3f(tran.multiply(new Vector4f(getPlaneCenter(), 1)).toArray(), 0));
		setPlaneNormal(new Vector3f(tran.multiply(new Vector4f(getPlaneNormal(), 0)).toArray(), 0));
		setFrame1(new Vector3f(tran.multiply(new Vector4f(getFrame1(), 0)).toArray(), 0));
		setFrame2(new Vector3f(tran.multiply(new Vector4f(getFrame2(), 0)).toArray(), 0));
		super.applyTransformation(tran);
	}

	public Vector3f getPlaneCenter() {
		return center_;
	}

	public void setPlaneCenter(Vector3f center_) {
		this.center_ = center_;
	}

	public Vector3f getPlaneNormal() {
		return normal_;
	}

	public void setPlaneNormal(Vector3f normal_) {
		this.normal_ = normal_;
	}

	public Vector3f getFrame1() {
		return frame1_;
	}

	public void setFrame1(Vector3f frame1_) {
		this.frame1_ = frame1_;
	}

	public Vector3f getFrame2() {
		return frame2_;
	}

	public void setFrame2(Vector3f frame2_) {
		this.frame2_ = frame2_;
	}

}
