package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import zhl.Android.Multitouch.render.ZColor;
import zhl.Android.Multitouch.render.ZProjector;

public class ZReferencePlane extends ZObject3D {
	
	public static final String LOG_TAG = ZReferencePlane.class.getSimpleName();
	
	private int gridBlocks_ = 40;
	private float gridSize_ = 4.f;
	private float []planeColor_ = ZColor.colorGray;
	
	public ZReferencePlane() {
		buildMeshData();
		setVisable(true);
	}

	private void buildMeshData() {
		int blocks = this.gridBlocks_/2;
		float size = this.gridSize_;
		float step = size/blocks;
		float h = -1.f;
		float []verts = new float[(blocks*2+1)*3*4];
		short []indices = new short[(blocks*2+1)*4];
		int j=0, k=0;
		for (int i=-blocks; i<=blocks; i++, j+=3*4, k+=4) {
			verts[j+0] = i*step; verts[j+1] = h; verts[j+2] = -size;
			verts[j+3] = i*step; verts[j+4] = h; verts[j+5] = size;
			verts[j+6] = -size; verts[j+7] = h; verts[j+8] = i*step;
			verts[j+9] = size; verts[j+10] = h; verts[j+11] = i*step;
			for (int kk=0; kk<4; kk++) {
				indices[k+kk] = (short)(k+kk);
			}
		}
		setVertices(verts);
		setIndices(indices);
		setColor(planeColor_[0], planeColor_[1], planeColor_[2], planeColor_[3]);
	}

	@Override
	public void draw(GL10 gl) {
		gl.glDepthMask(false);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glLineWidth(1.f);
		updateObject(gl);
		if (this.isVisable()) {
			gl.glDrawElements(GL10.GL_LINES, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
			//gl.glDrawArrays(GL10.GL_LINES, 0, numOfIndices_);
			//gl.glDrawElements(GL10.GL_POINTS, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		}
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glDepthMask(true);
		//Log.d(LOG_TAG, "Draw reference plane.");
	}

	@Override
	public boolean pick(ZProjector proj, float x, float y) {
		return false;
	}

	@Override
	public void prepareBuffers() {
		
	}
	
	
}
