package zhl.Android.Multitouch.render;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zhl.Android.math.Matrix4f;
import zhl.Android.math.Trackball;
import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZMesh;
import zhl.Android.scenes.ZMeshGroup;
import zhl.Android.scenes.ZObject3D;
import zhl.Android.scenes.ZReferencePlane;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

public class ZRenderer implements GLSurfaceView.Renderer {
	
	private static final String LOG_TAG = ZRenderer.class.getSimpleName();
	
	private ZView view_ = null;
	private GL10 gl_ = null;
	private ZProjector projector_ = new ZProjector();

	private ZMeshRenderer meshRenderer_ = new ZMeshRenderer();
	private ZRenderOptions renderOpt_ = new ZRenderOptions();
	private Trackball globalTrackball_ = null;//new Trackball();
	
	private float width_ = 320.0f;
	private float height_ = 480.0f;
	private float viewportScaleRatio_ = (width_>height_ ? height_ : width_);
	
	private boolean showReferenceGrid_ = true;
	private boolean showReferenceFrame_ = true;
	private ZReferencePlane referencePlane_ = new ZReferencePlane();
	
 	public ZRenderer(ZView view) {
		this.view_ = view;
	}

	public Trackball getTrackball() { 
		return globalTrackball_;
	}


	public void onDrawFrame(GL10 gl) {
		//Log.d(LOG_TAG, "onDrawFrame()");
		setGL(gl);
		start3DRendering(gl);
		draw3DObjects(gl);
		if (showReferenceGrid_) {
			referencePlane_.draw(gl);
		}
		end3DRendering(gl);	
		
		start2DRendering(gl);
		drawTouchPoints(gl);
		end2DRendering(gl);
	}

	private void drawTouchPoints(GL10 gl) {
		view_.getFingerDetect().onDraw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onSurfaceChanged()");
		width_ = width;
		height_= height;
		initMeshViewMatrix(gl);
		initTrackball();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onSurfaceCreated()");
		setGL(gl);
		initOpenGL(gl);
		initTrackball();
		updateData(gl);
	}

	public ZMeshRenderer getMeshRenderer_() {
		return meshRenderer_;
	}

	public void setMeshRenderer_(ZMeshRenderer meshRenderer_) {
		this.meshRenderer_ = meshRenderer_;
	}
	
	private void initOpenGL(GL10 gl) {
		float [] backCol = renderOpt_.backgroundColor;
		gl.glClearColor(backCol[0], backCol[1], backCol[2], backCol[3]);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glPolygonOffset(1.f, 1.f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL10.GL_POINT_SMOOTH);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glEnable(GL10.GL_BLEND);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, renderOpt_.lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, renderOpt_.lightSpecular, 0);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		//gl.glColorMatrial();
		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, renderOpt_.specularRef, 0);
		gl.glMaterialx(GL10.GL_FRONT, GL10.GL_SHININESS, 128);
	}
	
	private void updateData(GL10 gl) {
		meshRenderer_.updateData(gl);
	}
	
	private void start3DRendering(GL10 gl) {
		//Matrix4f m = globalTrackball_.getMatrix();
		float [] backCol = renderOpt_.backgroundColor;
		gl.glClearColor(backCol[0], backCol[1], backCol[2], backCol[3]);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		Matrix4f m = globalTrackball_.getMatrix();//Matrix4f.identityMatrix();
		//Log.d(LOG_TAG, "m:"+m);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		//GLU.gluLookAt(gl, 0, 4, 8, 0, 0, 0, 0, 1, 0);
		gl.glMultMatrixf(getProjector().getLookAtM(), 0);
		gl.glMultMatrixf(m.transpose().toArray(), 0);
		gl.glMultMatrixf(getProjector().getModelM(), 0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		//gl.glDisable(GL10.GL_LIGHT0);
		//gl.glDisable(GL10.GL_LIGHTING);
	}
	
	private void end3DRendering(GL10 gl) {
		//gl.glMatrixMode(GL10.GL_PROJECTION);
		//gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}
	
	synchronized private void draw3DObjects(GL10 gl) {
		//ZMesh mesh = ZDataManager.getDataManager_().getSimpleMesh_();
		meshRenderer_.draw(gl);
	}
	
	private void initMeshViewMatrix(GL10 gl) {
		// content
		getProjector().setGL(gl);
		// viewport
		gl.glViewport(0, 0, (int)width_, (int)height_);
		getProjector().setViewPortM(new int[]{0, 0, (int)width_, (int)height_});
		
		// projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		//GLU.gluPerspective(gl, 45.f, width_/height_, 0.01f, 100f);
		ZProjector.perspectiveM(getProjector().getProjM(), 0, 45.f, width_/height_, 0.01f, 100f);
		gl.glMultMatrixf(getProjector().getProjM(), 0);
		
		// model view matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		Matrix.setLookAtM(getProjector().getLookAtM(), 0, 0, 4, 8, 0, 0, 0, 0, 1, 0);
	}
	
	private void initTrackball() {
		if (globalTrackball_==null)
			globalTrackball_ = new Trackball(width_, height_);
		else
			globalTrackball_.setBounds(width_, height_);
		setViewportScaleRatio((width_>height_ ? height_ : width_));
	}

	public float getViewportScaleRatio() {
		return viewportScaleRatio_;
	}
	public void setViewportScaleRatio(float viewportScaleRatio) {
		this.viewportScaleRatio_ = viewportScaleRatio;
	}
	public float getWidth() {
		return width_;
	}
	public void setWidth(float width_) {
		this.width_ = width_;
	}
	public float getHeight() {
		return height_;
	}
	public void setHeight(float height_) {
		this.height_ = height_;
	}
	
	public void addData(Object obj) {
		//if (obj instanceof ZMesh || obj instanceof ZMeshGroup) {
		if (obj instanceof ZObject3D ) {
			meshRenderer_.addMesh(obj);
		} else if (obj instanceof ArrayList) {
			@SuppressWarnings("unchecked")
			ArrayList<ZObject3D> objs = (ArrayList<ZObject3D>)obj;
			for (ZObject3D obj3D : objs) {
				meshRenderer_.addMesh(obj3D);
			}
		}
		Log.d(LOG_TAG, "addData finished.");
	}
	
	public void updateProjector() {
		Trackball tb = getTrackball();
		Matrix4f oldM = new Matrix4f(getProjector().getModelM());
		getProjector().setModelM(tb.getMatrix().multiply(oldM.transpose()).transpose().toArray());
		//tb.end();
	}
	
	public void endTransformation() {
		
	}

	public ZProjector getProjector() {
		return projector_;
	}

	public void setProjector(ZProjector projector_) {
		this.projector_ = projector_;
	}
	
	public GL10 getGL() {
		return gl_;
	}

	public void setGL(GL10 gl_) {
		this.gl_ = gl_;
	}
	
	
	/// 2D rendering
	public void start2DRendering(GL10 gl) {
		float w = view_.getWidth();
		float h = view_.getHeight();
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, w, 0, h);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING);
	}
	
	public void end2DRendering(GL10 gl) {
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
	}
	
}
