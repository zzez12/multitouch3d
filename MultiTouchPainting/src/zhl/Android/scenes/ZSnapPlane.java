package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector4f;

public class ZSnapPlane extends ZObject3D {
	private static float []snapPlaneDefaultColor_ = {0.f, 200.f/255, 0.f, 50.f/255};
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
		init();
		this.setVisable(false);
	}
	
	private void init() {
		prepareBuffers();
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		if (this.isVisable()==false) return;
		updateObject(gl);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDrawElements(GL10.GL_TRIANGLE_FAN, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		gl.glDrawElements(GL10.GL_LINE_STRIP, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		gl.glEnable(GL10.GL_CULL_FACE);
	}

	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareBuffers() {
		Vector3f f1 = getFrame1().times(1.2f);
		Vector3f f2 = getFrame2().times(1.2f);
		Vector3f c = getPlaneCenter().plus(getPlaneNormal().times(0.05f));
		Vector3f v1 = c.plus(f1).plus(f2);
		Vector3f v2 = c.minus(f1).plus(f2);
		Vector3f v3 = c.minus(f1).minus(f2);
		Vector3f v4 = c.plus(f1).minus(f2);
		float []verticePos = new float[4*3];
		short []indices = new short[4];
		float []colors = new float[4*4];
		verticePos[0] = v1.x_; verticePos[1] = v1.y_; verticePos[2] = v1.z_;
		verticePos[3] = v2.x_; verticePos[4] = v2.y_; verticePos[5] = v2.z_;
		verticePos[6] = v3.x_; verticePos[7] = v3.y_; verticePos[8] = v3.z_;
		verticePos[9] = v4.x_; verticePos[10] = v4.y_; verticePos[11] = v4.z_;
		for (int i=0; i<4; i++) {
			indices[i] = (short)i;
			colors[i*4+0] = snapPlaneDefaultColor_[0];
			colors[i*4+1] = snapPlaneDefaultColor_[1];
			colors[i*4+2] = snapPlaneDefaultColor_[2];
			colors[i*4+3] = snapPlaneDefaultColor_[3];
		}
		this.setVertices(verticePos);
		this.setIndices(indices);
		this.setColor(colors);
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
		//return center_;
		return getCurrentVector(center_, true, false);
	}

	public void setPlaneCenter(Vector3f center_) {
		this.center_ = center_;
	}

	public Vector3f getPlaneNormal() {
		//return normal_;
		return getCurrentVector(normal_, true, true);
	}

	public void setPlaneNormal(Vector3f normal_) {
		this.normal_ = normal_;
	}

	public Vector3f getFrame1() {
		//return frame1_;
		return getCurrentVector(frame1_, true, true);
	}

	public void setFrame1(Vector3f frame1_) {
		this.frame1_ = frame1_;
	}

	public Vector3f getFrame2() {
		//return frame2_;
		return getCurrentVector(frame2_, true, true);
	}

	public void setFrame2(Vector3f frame2_) {
		this.frame2_ = frame2_;
	}

}
