package zhl.Android.scenes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import zhl.Android.Multitouch.touch.ZTimer;

public class ZDataManager {
	private static ZDataManager dataManager_ = new ZDataManager();
		
	//private ArrayList<ZObject3D> allObject3D_ = new ArrayList<ZObject3D>();
	private Vector<ZObject3D> allObject3D_ = new Vector<ZObject3D>();
	
	private ZTimer timer_ = new ZTimer();
	
	// prevent from new instance
	private ZDataManager(){}

	public static ZDataManager getDataManager() {
		return dataManager_;
	}
	
//	public ArrayList<ZObject3D> getAllObject3D() {
//		return allObject3D_;
//	}
	
	public Vector<ZObject3D> getAllObject3D() {
		return allObject3D_;
	}
	
	synchronized public ZObject3D getObject3D(int index) {
		return allObject3D_.get(index);
	}
	
	synchronized public void addMesh(String str) throws FileNotFoundException, IOException {
		ZMesh mesh = new ZMesh();
		mesh.load(str);
		getDataManager().getAllObject3D().add(mesh);
	}
	
	public void resetAll() {
		for (ZObject3D obj : getAllObject3D()) {
			obj.reset();
		}
	}
	
	synchronized public void clearAll() {
		//allObject3D_ = new ArrayList<ZObject3D>();
		allObject3D_ = new Vector<ZObject3D>();
	}

	public ZTimer getTimer() {
		return timer_;
	}
}
