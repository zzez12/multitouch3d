package zhl.Android.Multitouch.render;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import zhl.Android.math.Vector3f;
import zhl.Android.scenes.ZLine3D;

public class ZProjector {
	private GL10 gl_=null;
	private float [] modelM_ = new float[16];	// column-first matrix
	private float [] projM_  = new float[16];	// column-first matrix
	private int [] view_ = new int[4];
	
	private float [] lookAtM_ = new float[16];	// save the "look at" matrix
	
	private Vector3f rayStart_ = new Vector3f();
	private Vector3f rayEnd_ = new Vector3f();
	
	protected float [] depthBuffer_ = null;

	public ZProjector() {
		init();
	}
	public ZProjector(GL10 gl, float [] model, float [] projM, int [] view) {
		setGL(gl);
		setModelM(model);
		setProjM(projM);
		setViewPortM(view);
	}
	
	public void init() {
		modelM_[0] = modelM_[5] = modelM_[10] = modelM_[15] = 1.f;
		Matrix.setLookAtM(getLookAtM(), 0, 0, 4, 8, 0, 0, 0, 0, 1, 0);
	}
	
	/*
	 * return lookAtM*modelM
	 */
	public float[] getViewModel() {
		float [] viewModel = new float[16];
		Matrix.multiplyMM(viewModel, 0, getLookAtM(), 0, getModelM(), 0);
		return viewModel;
	}
	
	public Vector3f unProject(float x, float y, float z) {
		float [] r = new float[4];
		GLU.gluUnProject(x, y, z, getViewModel(), 0, getProjM(), 0, getViewPortM(), 0, r, 0);
		for (int i=0; i<3; i++) {
			r[i] /= r[3];
		}
		return new Vector3f(r, 0);
	}
	public Vector3f unProject(Vector3f p) {
		return unProject(p.x_, p.y_, p.z_);
	}
	
	public void unProject(float x, float y) {
		//y = getViewPortM()[3] - y;
		float [] r1 = new float[4];
		float [] r2 = new float[4];
		GLU.gluUnProject(x, y, 0f, getViewModel(), 0, getProjM(), 0, getViewPortM(), 0, r1, 0);
		GLU.gluUnProject(x, y, 1f, getViewModel(), 0, getProjM(), 0, getViewPortM(), 0, r2, 0);
		for (int i=0; i<3; i++) {
			r1[i] /= r1[3];
			r2[i] /= r2[3];
		}
		rayStart_ = new Vector3f(r1, 0);
		rayEnd_ = new Vector3f(r2, 0);
	}
	
	public void project(float [] vrt3D, float[]vrt2DAndDepth) {
		GLU.gluProject(vrt3D[0], vrt3D[1], vrt3D[2], getViewModel(), 0, getProjM(), 0, getViewPortM(), 0, vrt2DAndDepth, 0);
	}
	
	public Vector3f project(Vector3f vrt) {
		return project(new float []{vrt.x_, vrt.y_, vrt.z_});
	}
	
	public Vector3f project(float []vrt3D) {
		float []vrt2DandDepth = new float[3];
		project(vrt3D, vrt2DandDepth);
		return new Vector3f(vrt2DandDepth, 0);
	}
	
	public float getDepthValue(int x, int y) {
		return 0.f;
	}
	
	public void updateDepthValue() {
		//gl_.glReadPixels(view_[0], view_[1], view_[2], view_[2], GL10.GL_DEPTH_BITS, type, pixels)
	}

	public Vector3f getRayEnd() {
		return rayEnd_;
	}

	public void setRayEnd(Vector3f rayEnd_) {
		this.rayEnd_ = rayEnd_;
	}

	public Vector3f getRayStart() {
		return rayStart_;
	}

	public void setRayStart(Vector3f rayStart_) {
		this.rayStart_ = rayStart_;
	}

	public float [] getModelM() {
		return modelM_;
	}

	public void setModelM(float [] modelM_) {
		this.modelM_ = modelM_;
	}

	public float [] getProjM() {
		return projM_;
	}

	public void setProjM(float [] projM_) {
		this.projM_ = projM_;
	}

	public int [] getViewPortM() {
		return view_;
	}

	public void setViewPortM(int [] view_) {
		this.view_ = view_;
	}
	

    /**
     * Define a projection matrix in terms of a field of view angle, an
     * aspect ratio, and z clip planes
     * @param m the float array that holds the perspective matrix
     * @param offset the offset into float array m where the perspective
     * matrix data is written
     * @param fovy field of view in y direction, in degrees
     * @param aspect width to height aspect ratio of the viewport
     * @param zNear
     * @param zFar
     */
    public static void perspectiveM(float[] m, int offset,
          float fovy, float aspect, float zNear, float zFar) {
        float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
        float rangeReciprocal = 1.0f / (zNear - zFar);

        m[offset + 0] = f / aspect;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;

        m[offset + 4] = 0.0f;
        m[offset + 5] = f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;

        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1.0f;

        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;
    }
	public float [] getLookAtM() {
		return lookAtM_;
	}
	public void setLookAtM(float [] lookAtM_) {
		this.lookAtM_ = lookAtM_;
	}
	public GL10 getGL() {
		return gl_;
	}
	public void setGL(GL10 gl_) {
		this.gl_ = gl_;
	}
	

	public ZLine3D getRayLineFromTouchPoint(float x, float y) {
		this.unProject(x, y);
		return new ZLine3D(getRayStart(), getRayEnd());
	}
}
