package zhl.Android.Multitouch.render;

import java.io.FileNotFoundException;
import java.io.IOException;

import zhl.Android.Multitouch.touch.ZFingerRegister;
import zhl.Android.Multitouch.touch.ZFingerRegisterListener;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Trackball;
import zhl.Android.math.Vector3f;
import zhl.Android.scenes.ZAxis;
import zhl.Android.scenes.ZCube;
import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZMesh;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class ZView extends GLSurfaceView {
	
	private static final String LOG_TAG = ZView.class.getSimpleName();
	
	public enum EnumOperationMode {
		Unknown,
		Translation, Scaling, Rotation, UniformScale,
		PlannerTranslation, PlannerScaling,
		GlobalPan, GlobalZoom, GlobalRotation,
		ActiveSnap, FreeRotation,
		Testing
	};
	
	private ZRenderer renderer_ = null;
	private GestureDetector gestureDetect_ = null;
	private ScaleGestureDetector scaleDetect_ = null;
	private ZFingerRegister fingerDetect_ = null;
	
	private EnumOperationMode operationMode_ = EnumOperationMode.Unknown;
	//private ZProjector projector_ = new ZProjector();

	public ZView(Context context) {
		super(context);
		//init();
		
		// prepare data
		try {
			initMesh();
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, "File not found.." + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(LOG_TAG, "IOException.." + e.getMessage());
			e.printStackTrace();
		}
		
		// prepare listener
		//setGestureDetect(new GestureDetector(new ZOneTouchListener(this)));
		//this.setLongClickable(true);
		//setScaleDetect(new ScaleGestureDetector(context, new ZTwoTouchListener(this)));
		fingerDetect_ = new ZFingerRegister(context, new ZFingerRegisterListener(this));
		//fingerDetect_ = new ZFingerRegister(context, null);
		
		// prepare renderer
		setViewRenderer(new ZRenderer(this));
		setRenderer(getRenderer());	
		getRenderer().addData(ZDataManager.getDataManager().getAllObject3D());
	}
	
//	private void init() {
//		//modelM_[0] = modelM_[5] = modelM_[10] = modelM_[15] = 1.f;
//	}
	
	public Trackball getTrackball() { 
		return getRenderer().getTrackball();
	}
	
	public void initMesh() throws FileNotFoundException, IOException {
		//ZMeshGroup meshGroup = ZDataManager.getDataManager_().getMeshGroup();
		//meshGroup.addMesh(ZMesh.buildSimpleMesh());
		//meshGroup.addMesh(new ZCube(2.f, 1.f, 3.f));
		ZMesh mesh = new ZMesh();
		mesh.load("/sdcard/01.obj");
		//mesh.load("/sdcard/02.obj");
		mesh.buildAxes();
		//meshGroup.addMesh(mesh);
		ZDataManager.getDataManager().getAllObject3D().add(mesh);
		ZMesh mesh2 = new ZMesh();
		mesh2.load("/sdcard/02.obj");
		mesh2.buildAxes();
		Matrix4f trans = Matrix4f.identityMatrix();
		trans.set(0, 3, 0.5f);
		mesh2.applyTransformation(trans);
		ZDataManager.getDataManager().getAllObject3D().add(mesh2);
//		ZDataManager.getDataManager().getAllObject3D().add(new ZCube(2.f, 1.f, 3.f));
//		ZAxis xAxis = new ZAxis(new Vector3f(0.f,0.f,0.f), new Vector3f(1.f,0.f,0.f), ZColor.colorRed, ZAxis.AxisType.ScreenAxis);
//		ZDataManager.getDataManager().getAllObject3D().add(xAxis);
		Log.d(LOG_TAG, "Mesh initialized.");
	}

    public boolean onTouchEvent(MotionEvent event) {   
    	int count = event.getPointerCount();
    	Log.d(LOG_TAG, "touch event, finger: " + count);
    	this.fingerDetect_.onTouchEvent(event);
//    	if (count==1) {
//    		return getGestureDetect().onTouchEvent(event);
//    	}
//    	else if(count==2) {
//    		return getScaleDetect().onTouchEvent(event);
//    	}

    	return true;
    	//return gestureDetect_.onTouchEvent(event);
    }

	public EnumOperationMode getOperationMode() {
		return operationMode_;
	}

	public void setOperationMode(EnumOperationMode operationMode) {
		this.operationMode_ = operationMode;
	}
	
	public float getViewportScaleRatio() {
		return getRenderer().getViewportScaleRatio();
	}
	
	public float getWindowWidth() {
		return getRenderer().getWidth();
	}

	public GestureDetector getGestureDetect() {
		return gestureDetect_;
	}

	public void setGestureDetect(GestureDetector gestureDetect_) {
		this.gestureDetect_ = gestureDetect_;
	}

	public ScaleGestureDetector getScaleDetect() {
		return scaleDetect_;
	}

	public void setScaleDetect(ScaleGestureDetector scaleDetect_) {
		this.scaleDetect_ = scaleDetect_;
	}
	
	public ZFingerRegister getFingerDetect() {
		return this.fingerDetect_;
	}

	public void endTransformation() {
		getRenderer().updateProjector();
		setOperationMode(EnumOperationMode.Unknown);
	}


	public ZProjector getProjector() {
		return getRenderer().getProjector();
	}
//
//	public void setProjector(ZProjector projector_) {
//		this.projector_ = projector_;
//	}

	public ZRenderer getRenderer() {
		return renderer_;
	}

	public void setViewRenderer(ZRenderer renderer_) {
		this.renderer_ = renderer_;
	}

}
