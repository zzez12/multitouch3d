package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.math.Vector3f;

public class ZLine3D extends ZObject3D {
	public Vector3f startP_ = new Vector3f();
	public Vector3f endP_ = new Vector3f();
	
	public ZLine3D() {}
	
	public ZLine3D(Vector3f start, Vector3f end) {
		this.startP_ = start;
		this.endP_ = end;
		//buildBuffers();
	}
	
	public void setLine(Vector3f start, Vector3f end) {
		this.startP_ = start;
		this.endP_ = end;
		this.makeDirty();
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		if (isDirty()) prepareBuffers();
		updateObject(gl);
		gl.glMultMatrixf(getWorldTransformation().transpose().getMatrix(), 0);
		gl.glDrawElements(GL10.GL_LINES, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ZLine: ("+startP_.x_+","+startP_.y_+","+startP_.z_+") --> ("
						 +endP_.x_ + ","+endP_.y_ + ","+endP_.z_ +")";
		//return super.toString();
	}

	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareBuffers() {
		// TODO Auto-generated method stub
		float [] vertices = new float[2*3];
		short [] indices = new short[2];
		vertices[0] = startP_.x_; vertices[1] = startP_.y_; vertices[2] = startP_.z_;
		vertices[3] = endP_.x_; vertices[4] = endP_.y_; vertices[5] = endP_.z_;
		//this.numOfVertices_ = 2;
		indices[0] = 0; indices[1] = 1;
		//this.numOfIndices_ = 2;
		this.setVertices(vertices);
		this.setIndices(indices);
		makeUnDirty();
	}
	
}
