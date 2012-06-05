package zhl.Android.Multitouch.render;

import java.util.ConcurrentModificationException;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZMesh;
import zhl.Android.scenes.ZMeshGroup;
import zhl.Android.scenes.ZObject3D;

public class ZMeshRenderer {
	
	private static final String TAG_LOG = ZMeshRenderer.class.getSimpleName();
	
	private Vector<Object> toRenderObjs = new Vector<Object>();
	
	synchronized public void draw(GL10 gl) {
		try {
			for (ZObject3D obj : ZDataManager.getDataManager().getAllObject3D()) {
				obj.draw(gl);
			}
		}
		catch(ConcurrentModificationException e) {
			// catch but not deal with it now..
			Log.d(TAG_LOG, e.toString());
		}

	}
	
//	public void addMesh(Object obj) {
//		if (!hasMesh(obj))
//			getToRenderObjs().add(obj);
//	}
//	
//	public boolean hasMesh(Object o) {
//		for (Object obj:getToRenderObjs()) {
//			if (obj.equals(o))
//				return true;
//		}
//		return false;
//	}
//	
//	public void updateData(GL10 gl) {
//		for (Object obj:getToRenderObjs()) {
//			if (obj instanceof ZObject3D) {
//				ZObject3D obj3d = (ZObject3D)obj;
//				//obj3d.updateObject(gl);
//				obj3d.makeDirty(); // set dirty tag, then it will be updated when rendering
//			}
//		}
//	}
//
//	public Vector<Object> getToRenderObjs() {
//		return toRenderObjs;
//	}
//
//	public void setToRenderObjs(Vector<Object> toRenderObjs) {
//		this.toRenderObjs = toRenderObjs;
//	}
	
}
