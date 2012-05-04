package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZColor;
import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.math.Vector3f;

public class ZPolygon3D extends ZObject3D {
	
	private Vector3f[] polygonVertices_ = null;
	private float[] color_ = ZColor.fromRGBA(0.f, 200.f/255.f, 0.f, 50.f/255.f);
	
	public ZPolygon3D(Vector3f[] vrts) {
		polygonVertices_ = new Vector3f[vrts.length];
		for (int i=0; i<vrts.length; i++) {
			polygonVertices_[i] = new Vector3f(vrts[i]);
		}
		prepareBuffers();
	}

	@Override
	public void draw(GL10 gl) {
		updateObject(gl);
		gl.glPushMatrix();
		gl.glMultMatrixf(getWorldTransformation().transpose().getMatrix(), 0);
		gl.glDrawElements(GL10.GL_TRIANGLE_FAN, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		gl.glDrawElements(GL10.GL_LINE_STRIP, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		gl.glPopMatrix();
	}

	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareBuffers() {
		int size = polygonVertices_.length;
		float []vrtPos = new float[size*3];
		short []indices = new short[size];
		float []colors = new float[size*4];
		for (int i=0, j=0; i<size; i++, j+=3) {
			vrtPos[j] = polygonVertices_[i].x_;
			vrtPos[j+1] = polygonVertices_[i].y_;
			vrtPos[j+2] = polygonVertices_[i].z_;
			colors[i*4+0] = color_[0];
			colors[i*4+1] = color_[1];
			colors[i*4+2] = color_[2];
			colors[i*4+3] = color_[3];
			indices[i] = (short)i;
		}
		
		setVertices(vrtPos);
		//setColor(color_[0], color_[1], color_[2], color_[3]);
		setColor(colors);
		setIndices(indices);
	}

}
