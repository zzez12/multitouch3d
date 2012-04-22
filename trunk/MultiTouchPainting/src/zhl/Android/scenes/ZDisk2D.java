package zhl.Android.scenes;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZColor;

public class ZDisk2D extends ZMesh {
	
	private float innerRadius_;
	private float outerRadius_;
	private int slices_;
	private int loops_;
	
	public ZDisk2D(float innerRadius, float outerRadius, int slices, int loops) {
		this.innerRadius_ = innerRadius;
		this.outerRadius_ = outerRadius;
		this.slices_ = slices;
		this.loops_ = loops;
		buildMeshData();
	}
	
	public void buildMeshData() {
		float []vertices = new float[slices_*3*2];
		float []colors = new float[slices_*4*2];
		//float []normals = new float[slices_*3*2];
		short []faceIndices = new short[slices_*3*2];
		int offset = slices_*3;
		float []colorOuter = ZColor.fromRGBA(200.f/255, 255.f/255, 50.f/255, 50.f/255);
		float []colorInner = ZColor.fromRGBA(50.f/255, 255.f/255, 200.f/255, 50.f/255);
		for (int i=0; i<slices_; i++) {
			float cosA = (float)Math.cos(2.0*i/slices_*Math.PI);
			float sinA = (float)Math.sin(2.0*i/slices_*Math.PI);
			// positions
			vertices[i*3+0] = innerRadius_*cosA;
			vertices[i*3+1] = innerRadius_*sinA;
			vertices[i*3+2] = 0.f;		
			vertices[i*3+0 + offset] = outerRadius_*cosA;
			vertices[i*3+1 + offset] = outerRadius_*sinA;
			vertices[i*3+2 + offset] = 0.f;
			// colors
			System.arraycopy(colorInner, 0, colors, i*4, 4);
			System.arraycopy(colorOuter, 0, colors, i*4+slices_*4, 4);
			// faces
			faceIndices[i*3+0] = (short)i;
			faceIndices[i*3+1] = (short)((i+1)%slices_ + slices_);
			faceIndices[i*3+2] = (short)((i+1)%slices_);
			faceIndices[i*3+0+offset] = (short)i;
			faceIndices[i*3+1+offset] = (short)((i + slices_));
			faceIndices[i*3+2+offset] = (short)((i+1)%slices_+slices_);
		}
		super.buildMeshData(vertices, faceIndices, colors, ZMesh.buildNormals(vertices, faceIndices));
		makeDirty();
	}
}
