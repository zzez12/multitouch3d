package zhl.Android.scenes;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class ZMeshGroup extends ZMesh {
	private static final String LOG_TAG = ZMeshGroup.class.getSimpleName();
	
	protected Vector<ZMesh> meshes = new Vector<ZMesh>();
	
	public ZMeshGroup() {
		
	}
	
	public void draw(GL10 gl) {
		int count = 0;
		for (ZMesh mesh:meshes) {
			mesh.draw(gl);
			count ++;
		}
		Log.d(LOG_TAG, " Group: " + count);
	}
	
	public void addMesh(int location, ZMesh mesh) {
		meshes.add(location, mesh);
	}
	
	public boolean addMesh(ZMesh mesh) {
		return meshes.add(mesh);
	}
	
	public void clearMeshes() {
		meshes.clear();
	}
	
	public ZMesh getMesh(int location) {
		return meshes.get(location);
	}
	
	public ZMesh removeMesh(int location) {
		return meshes.remove(location);
	}

	public int size() {
		return meshes.size();
	}
}
