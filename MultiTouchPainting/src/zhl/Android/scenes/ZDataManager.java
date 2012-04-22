package zhl.Android.scenes;

import java.util.ArrayList;

public class ZDataManager {
	private static ZDataManager dataManager_ = new ZDataManager();
		
	private ArrayList<ZObject3D> allObject3D_ = new ArrayList<ZObject3D>();
	
	// prevent from new instance
	private ZDataManager(){}

	public static ZDataManager getDataManager() {
		return dataManager_;
	}
	
	public ArrayList<ZObject3D> getAllObject3D() {
		return allObject3D_;
	}
	
	public ZObject3D getObject3D(int index) {
		return allObject3D_.get(index);
	}

//	public ZMesh getSimpleMesh_() {
//		if (simpleMesh_==null)
//			simpleMesh_ = ZMesh.buildSimpleMesh();//new ZMesh();
//		return simpleMesh_;
//	}
//	
//	public ZMeshGroup getMeshGroup() {
//		if (meshGroup_==null)
//			meshGroup_ = new ZMeshGroup();
//		return meshGroup_;
//	}
//
//	public void setSimpleMesh_(ZMesh simpleMesh_) {
//		this.simpleMesh_ = simpleMesh_;
//	}
}
