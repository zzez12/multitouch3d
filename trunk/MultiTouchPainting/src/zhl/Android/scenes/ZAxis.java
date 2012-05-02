package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Vector2f;
import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector4f;

public class ZAxis extends ZObject3D{
	public enum AxisType { ObjectAxis, ReferenceAxis, ReferencePivotAxis, GlobalAxis, ScreenAxis };
	
	private Vector3f oriP_ = new Vector3f();
	private Vector3f dirP_ = new Vector3f(1,0,0);
	private AxisType type_ = AxisType.ObjectAxis;
	private float[] axisColor_ = new float[4];
	private float[] axisColorEx_ = new float[]{1.f, 0.5f, 0.5f, 0.5f};
	
	private Vector2f projectedAxis_ = null;
	private float projectionScale_ = 0.f;

	
	public ZAxis(Vector3f oriP, Vector3f dirP) {
		this.setOri(oriP);
		this.setDir(dirP);
	}
	
	public ZAxis(Vector3f oriP, Vector3f dirP, float []color, AxisType type) {
		this.setOri(oriP);
		this.setDir(dirP);
		this.setAxisColor(color);
		this.setType(type);
	}
	
	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	public void prepareBuffers() {
		float extraLen = 1.f;
		if (this.getType() == AxisType.ScreenAxis) extraLen = 5;
		float ratio = 0.5f;
		
		Vector3f a = dirP_.plus( dirP_.normalize().times(extraLen) );
		//Vector3f b = dirP_.plus( dirP_.normalize().times(extraLen*0.5f) );
		Vector3f c = oriP_;
		Vector3f v1 = c.plus(a);
		Vector3f v2 = c.minus(a);
		Vector3f u1 = c.plus(a.times(ratio));
		Vector3f u2 = c.minus(a.times(ratio));
		
		float []verticePos = new float[5*3];
		// v2<-- u2 <-- c <-- u1 <-- v1
		// 4      3     0     1      2
		verticePos[0] = c.x_; verticePos[1] = c.y_; verticePos[2] = c.z_;
		verticePos[3] = u1.x_; verticePos[4] = u1.y_; verticePos[5] = u1.z_;
		verticePos[6] = v1.x_; verticePos[7] = v1.y_; verticePos[8] = v1.z_;
		verticePos[9] = u2.x_; verticePos[10] = u2.y_; verticePos[11] = u2.z_;
		verticePos[12] = v2.x_; verticePos[13] = v2.y_; verticePos[14] = v2.z_;
		//short [] lineIdx = {0,1, 1,2, 0,3, 3,4};
		short [] lineIdx = {4, 3, 0, 1, 2};
		float [] colors = new float[5*4];
		float R = axisColor_[0];
		float G = axisColor_[1];
		float B = axisColor_[2];
		//float A = axisColor_[3];
		for (int i=0; i<5; i++) {
			colors[i*4] = R; colors[i*4+1] = G; colors[i*4+2] = B; colors[i*4+3] = 1.f;
		}
		colors[2*4+3] = 0.f;
		colors[3*4+3] = 0.f;
		
		this.setVertices(verticePos);
		this.setColor(colors);
		this.setIndices(lineIdx);
		makeUnDirty();
	}
	
	public void prepareBuffersEx() {
		float extraLen = 1.5f;
		if (this.getType() == AxisType.ScreenAxis) extraLen = 5;
		float ratio = 0.5f;
		
		Vector3f a = dirP_.plus( dirP_.normalize().times(extraLen) );
		//Vector3f b = dirP_.plus( dirP_.normalize().times(extraLen*0.5f) );
		Vector3f c = oriP_;
		Vector3f v1 = c.plus(a);
		Vector3f v2 = c.minus(a);
		Vector3f u1 = c.plus(a.times(ratio));
		Vector3f u2 = c.minus(a.times(ratio));
		
		float []verticePos = new float[5*3];
		// v2<-- u2 <-- c <-- u1 <-- v1
		// 4      3     0     1      2
		verticePos[0] = c.x_; verticePos[1] = c.y_; verticePos[2] = c.z_;
		verticePos[3] = u1.x_; verticePos[4] = u1.y_; verticePos[5] = u1.z_;
		verticePos[6] = v1.x_; verticePos[7] = v1.y_; verticePos[8] = v1.z_;
		verticePos[9] = u2.x_; verticePos[10] = u2.y_; verticePos[11] = u2.z_;
		verticePos[12] = v2.x_; verticePos[13] = v2.y_; verticePos[14] = v2.z_;
		//short [] lineIdx = {0,1, 1,2, 0,3, 3,4};
		short [] lineIdx = {4, 3, 0, 1, 2};
		float [] colors = new float[5*4];
		float R = axisColorEx_[0];
		float G = axisColorEx_[1];
		float B = axisColorEx_[2];
		//float A = axisColor_[3];
		for (int i=0; i<5; i++) {
			colors[i*4] = R; colors[i*4+1] = G; colors[i*4+2] = B; colors[i*4+3] = 1.f;
		}
		colors[2*4+3] = 0.f;
		colors[3*4+3] = 0.f;
		
		this.setVertices(verticePos);
		this.setColor(colors);
		this.setIndices(lineIdx);
		makeUnDirty();
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		//if (isDirty()) prepareBuffers();
		prepareBuffers();
		updateObject(gl);
		//getGLStatus(gl);
		//gl.glLineWidth(8.f);
		
		gl.glDisable(GL10.GL_LIGHTING);
		if (this.isVisable()) {
			gl.glLineWidthx(8);	// unsupported by HTC DesireHD!!
			gl.glDrawElements(GL10.GL_LINE_STRIP, 5, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
			gl.glPointSize(10.f);
			gl.glDrawElements(GL10.GL_POINTS, 5, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		}
		gl.glEnable(GL10.GL_LIGHTING); 
	}
	
	public void drawEx(GL10 gl) {
		prepareBuffersEx();
		updateObject(gl);
		
		if (this.isVisable()) {
			gl.glLineWidthx(8);	// unsupported by HTC DesireHD!!
			gl.glDrawElements(GL10.GL_LINE_STRIP, 5, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
			gl.glPointSize(10.f);
			gl.glDrawElements(GL10.GL_POINTS, 5, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		}
	}

	/*
	 * return the input ori position
	 */
	public Vector3f getOri() {
		return oriP_;
	}
	
	/*
	 * return the transformed ori position
	 */
	public Vector3f getCurrentOri(boolean bWithTmpTrans) {
		return getCurrentVector(getOri(), bWithTmpTrans, false);
	}

	public void setOri(Vector3f oriP_) {
		this.oriP_ = oriP_;
	}

	public Vector3f getDir() {
		return dirP_;
	}
	
	public Vector3f getCurrentDir(boolean bWithTmpTrans) {
		return getCurrentVector(getDir(), bWithTmpTrans, true); 
	}

	public void setDir(Vector3f dirP_) {
		this.dirP_ = dirP_;
	}

	public AxisType getType() {
		return type_;
	}

	public void setType(AxisType type_) {
		this.type_ = type_;
	}

	public float[] getAxisColor() {
		return axisColor_;
	}

	public void setAxisColor(float[] axisColor_) {
		this.axisColor_ = axisColor_;
	}
	
	public void setAxisColorEx(float[] axisColor) {
		this.axisColorEx_ = axisColor;
	}
	
	public void getGLStatus(GL10 gl) {
		int []widths = new int[2];
		gl.glGetIntegerv(GL10.GL_SMOOTH_LINE_WIDTH_RANGE, widths, 0);
		int []pWidths = new int[2];
		gl.glGetIntegerv(GL10.GL_SMOOTH_POINT_SIZE_RANGE, pWidths, 0);
		Log.d(LOG_TAG, ""+widths[0] + "," + widths[1] + "," + pWidths[0] + "," + pWidths[1]);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_DONT_CARE);
	}

	public float getProjectionScale() {
		return projectionScale_;
	}

	public void setProjectionScale(float projecionScale) {
		this.projectionScale_ = projecionScale;
	}

	public Vector2f getProjectedAxis() {
		return projectedAxis_;
	}

	public void setProjectedAxis(Vector2f projectedAxis_) {
		this.projectedAxis_ = projectedAxis_;
	}

	@Override
	public void applyTransformation(Matrix4f tran) {
		// TODO Auto-generated method stub
		if (getType() != AxisType.ReferencePivotAxis) {
			this.setObjCenter(new Vector3f(tran.transpose().multiply(new Vector4f(getOri(), 1)).toArray(), 0));
		}
		if (getType() == AxisType.ObjectAxis) {
			this.setDir(new Vector3f(tran.transpose().multiply(new Vector4f(getDir(), 0)).toArray(), 0));
		}
		super.applyTransformation(tran);
	}
	
	
}
