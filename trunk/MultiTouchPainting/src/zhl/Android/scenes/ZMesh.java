package zhl.Android.scenes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZColor;
import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.Multitouch.render.ZRenderOptions;
import zhl.Android.Multitouch.render.ZView.EnumOperationMode;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector4f;
import zhl.Android.math.ZAlgorithms;
import zhl.Android.math.ZAlgorithms.TriangleIntersection;

import android.util.Log;

public class ZMesh extends ZObject3D implements ZMeshIO{
	private final static String LOG_TAG = ZMesh.class.getSimpleName();
	public final static int FACE_EDGE_NUM = 3;
	public final static int COLOR_FORMAT = 4;
	
	public final static String preFix = "/sdcard/models/"; 
	public final static String strName01 = preFix + "01.obj";
	public final static String strNameCylinder = preFix + "cylinder.obj";
	public final static String strNameCone = preFix + "cone.obj";
	public final static String strNameCube = preFix + "cube.obj";
	public final static String strNameElk = preFix + "elk.obj";
	public final static String strNameSphere = preFix + "sphere.obj";
	public final static String strNameTable = preFix + "table_more.obj";
	
	private boolean bKeepMeshData = true;
	
	protected class ZSimpleMeshData {
		public float [] verticesPos_ = null;
		public float [] verticesColor_ = null;
		public short [] faceIndices_ = null;
		public float [] normals_ = null;
		public int nVertices_;
		public int nFaces_;
		public ZSimpleMeshData(float [] verticesPos, short []faceIndices, float [] verticesColor, float [] normal) {
			setVertices(verticesPos);
			setIndices(faceIndices);
			setColor(verticesColor);
			setNormals(normal);
		}
		public boolean isValid() {
			return verticesPos_!=null && faceIndices_!=null;
		}
		public void setVertices(float [] pos) {
			verticesPos_ = pos;
			nVertices_ = pos.length/ZMesh.FACE_EDGE_NUM;
		}
		public void setColor(float [] col) {
			if (col==null) return;
			verticesColor_ = col;
		}
		public void setIndices(short [] indices) {
			faceIndices_ = indices;
			nFaces_ = indices.length/ZMesh.FACE_EDGE_NUM;
		}
		public float [] getNormals() {
			return normals_;
		}
		public void setNormals(float [] normals) {
			if (normals==null) return;
			this.normals_ = normals;
		}
		public Vector3f getVertex(int index) {
			return new Vector3f(verticesPos_[index*3], verticesPos_[index*3+1], verticesPos_[index*3+2]); 
		}
		public void setColorAt(int index, float[] color) {
			if (verticesColor_==null) {
				verticesColor_ = new float[nVertices_*4];
				for (int i=0; i<nVertices_*4; i++) verticesColor_[i] = 1.f;
			}
			for (int i=0; i<4; i++) {
				verticesColor_[index*4+i] = color[i];
			}
			makeDirty();
		}
	}
	
	protected ZSimpleMeshData meshData_ = null;
	
	//private Matrix4f transformMatrix_ = Matrix4f.identityMatrix();
	
	static public ZMesh buildSimpleMesh() {
		// simple mesh, just for testing
		ZMesh mesh = new ZMesh();
		Log.d(LOG_TAG, "Creating Mesh...");
        float[] coords = {
                -0.5f, -0.5f, 0.5f, // 0
                0.5f, -0.5f, 0.5f, // 1
                0f, -0.5f, -0.5f, // 2
                0f, 0.5f, 0f, // 3
        };
        //mesh.nVertices_ = coords.length/FACE_EDGE_NUM;
        
        float[] colors = {
                1f, 0f, 0f, 1f, // point 0 red
                0f, 1f, 0f, 1f, // point 1 green
                0f, 0f, 1f, 1f, // point 2 blue
                1f, 1f, 1f, 1f, // point 3 white
        };
        
        short[] indices = new short[] {
                0, 1, 3, // rwg
                0, 2, 1, // rbg
                0, 3, 2, // rbw
                1, 2, 3, // bwg
        };

        mesh.setVertices(coords);
        mesh.setColor(colors);
        mesh.setIndices(indices);
		Log.d(LOG_TAG, "Mesh created.");
		return mesh;
	}
	
	public ZMesh() {
		super();
		this.setVisable(true);
	}
	
//	private boolean parseOBJ() {
//		return true;
//	}

	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		//updateObject(gl);
		if (isDirty()) {
			prepareBuffers();
		}
		updateObject(gl);
		gl.glPushMatrix();
		gl.glMultMatrixf(getWorldTransformation().transpose().getMatrix(), 0);
		if (this.isVisable()) {
			gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices_, GL10.GL_UNSIGNED_SHORT, indicesBuffer_);
		}
		// draw children objects
		if (this.isFocused()) {
			for (ZObject3D obj : getChildObjects()) {
				if (obj instanceof ZAxis) {
					ZAxis axis = (ZAxis)obj;
					if (axis==this.getSelectedAxis()) {
						axis.drawEx(gl);
					}
					else {
						axis.draw(gl);
					}
				}
				else {
					obj.draw(gl);
				}
			}			
		}
		gl.glPopMatrix();

	}

	public boolean load(String fileName) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		ZParseOBJ.ZBuffers buffer = ZParseOBJ.parse(new FileReader(fileName));
		float [] normals = buildNormals(buffer.verticesPos, buffer.faceIndices);
		this.buildMeshData(buffer.verticesPos, buffer.faceIndices, null, normals);
		updateCenter();
		makeDirty();
		this.setName(fileName);
		return true;
	}

	public boolean save(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see zhl.Android.data.ZObject3D#pick(zhl.Android.Multitouch.render.ZProjector, float, float)
	 */
	public boolean pick(ZProjector proj, float x, float y) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, this.getName() + "--Begin to pick.");
		getPickedObjs().clear();
		proj.unProject(x, y);
		Vector3f stPt = proj.getRayStart();
		Vector3f edPt = proj.getRayEnd();
		TreeMap<Float, Integer> tmPickedObjs = new TreeMap<Float, Integer>();
		int count = 0;
		for (int i=0; i<meshData_.nFaces_; i++) {
			int vIdx0 = meshData_.faceIndices_[i*3];
			int vIdx1 = meshData_.faceIndices_[i*3+1];
			int vIdx2 = meshData_.faceIndices_[i*3+2];
			Vector3f v0 = getVertex(vIdx0);//meshData_.getVertex(vIdx0);
			Vector3f v1 = getVertex(vIdx1);//meshData_.getVertex(vIdx1);
			Vector3f v2 = getVertex(vIdx2);//meshData_.getVertex(vIdx2);
			//Vector3f intersect = new Vector3f();
			TriangleIntersection intersect = ZAlgorithms.intersect_RayTriangle(stPt, edPt, v0, v1, v2);
			//int bIntersect = ZAlgorithms.intersect_RayTriangle(stPt, edPt, v0, v1, v2, intersect);
			//Log.d(LOG_TAG, ""+intersect.intersectionP);
			if (intersect.intersection==1) {
				// compute the distance from the stPt to the intersection point
				float dis = intersect.intersectionP.minus(stPt).length();
				//pickedObjs_.add(i);
				tmPickedObjs.put(dis, i);
				count ++;
//				meshData_.setColorAt(vIdx0, ZColor.colorRed);
//				meshData_.setColorAt(vIdx1, ZColor.colorRed);
//				meshData_.setColorAt(vIdx2, ZColor.colorRed);
			}
		}
		// save the picked objs
		String strPicked = "" + tmPickedObjs.size() + "(" + count + ")" + ": ";
//		for (Integer it : tmPickedObjs.values()) {
//			pickedObjs_.add(it);
//			strPicked += it + " ";
//		}
		for (Float f : tmPickedObjs.keySet()) {
			getPickedObjs().add(f);
			strPicked += f + " ";
		}
//		String strPicked = ""+pickedObjs_.size() + ":";
//		for (Object obj:pickedObjs_) {
//			if (obj instanceof Integer) {
//				int o = ((Integer)obj).intValue();
//				strPicked += o + " ";
//			}
//		}
		Log.d(LOG_TAG, strPicked);
		if (!getPickedObjs().isEmpty()) {
			//this.setSelected(true);
			return true;
		} 
		else
		{
			//this.setSelected(false);
			return false;
		}
		//return !getPickedObjs().isEmpty();
	}
	
	public void updateVerticeColor() {
		if (!bKeepMeshData || meshData_==null) return;
		
		this.setColor(meshData_.verticesColor_);
	}
	
	public void updateVerticePos() {
		if (!bKeepMeshData || meshData_==null) return;
		this.setVertices(meshData_.verticesPos_);
	}
	
	public void updateNormal() {
		if (!bKeepMeshData || meshData_==null) return;
		this.setNormal(meshData_.normals_);
	}

	public void buildMeshData(float [] vrtPos, short [] faceIdx, float [] vrtColor, float [] normal) {
		if (!bKeepMeshData) return;
		
		meshData_ = new ZSimpleMeshData(vrtPos, faceIdx, vrtColor, normal); 
	}

	static final public float[] buildNormals(float [] verticesPos, short [] faceIndices) {
		if (verticesPos==null || faceIndices==null)
			return null;
		
		int nVertices = verticesPos.length/3;
		int nFaces = faceIndices.length/ZMesh.FACE_EDGE_NUM;
		float [] normals = new float[nVertices*3];
		// each face has a normal
		ArrayList<Vector3f> faceNormals = new ArrayList<Vector3f>(nFaces);
		for (int i=0; i<nFaces; i++) {
			short v0 = faceIndices[i*3];
			short v1 = faceIndices[i*3+1];
			short v2 = faceIndices[i*3+2];
			Vector3f p0 = new Vector3f(verticesPos[v0*3], verticesPos[v0*3+1], verticesPos[v0*3+2]);
			Vector3f p1 = new Vector3f(verticesPos[v1*3], verticesPos[v1*3+1], verticesPos[v1*3+2]);
			Vector3f p2 = new Vector3f(verticesPos[v2*3], verticesPos[v2*3+1], verticesPos[v2*3+2]);
			Vector3f n = (p1.minus(p0).cross(p2.minus(p0))).normalize();
			faceNormals.add(n);
		}
		List []vnf = new ArrayList[nVertices];
		for (int i=0; i<nVertices; i++) {
			vnf[i] = new ArrayList<Integer>();
		}
		for (int i=0; i<nFaces; i++) {
			short v0 = faceIndices[i*3];
			short v1 = faceIndices[i*3+1];
			short v2 = faceIndices[i*3+2];
			vnf[v0].add(i);
			vnf[v1].add(i);
			vnf[v2].add(i);
		}
		for (int i=0; i<nVertices; i++) {
			Vector3f n = new Vector3f();
			for (Object o:vnf[i]) {
				Integer j = (Integer)o;
				Vector3f fn = faceNormals.get(j.intValue());
				n = n.plus(fn);
			}
			n = n.normalize();
			normals[i*3+0] = n.x_;
			normals[i*3+1] = n.y_;
			normals[i*3+2] = n.z_;
		}
		return normals;
	}

	@Override
	public void prepareBuffers() {
		// TODO Auto-generated method stub
		this.setVertices(meshData_.verticesPos_);
		if (meshData_.verticesColor_!=null) this.setColor(meshData_.verticesColor_);
		if (meshData_.normals_!=null) this.setNormal(meshData_.normals_);
		this.setIndices(meshData_.faceIndices_);
		makeUnDirty();
	}
	
	public void buildAxes() {
		// simply building axis as x-axis, y-axis, and z-axis
		ZAxis xAxis = new ZAxis(objCenter_, new Vector3f(1.f,0.f,0.f), ZColor.axisColorX, ZAxis.AxisType.ObjectAxis);
		ZAxis yAxis = new ZAxis(objCenter_, new Vector3f(0.f,1.f,0.f), ZColor.axisColorY, ZAxis.AxisType.ObjectAxis);
		ZAxis zAxis = new ZAxis(objCenter_, new Vector3f(0.f,0.f,1.f), ZColor.axisColorZ, ZAxis.AxisType.ObjectAxis);
		this.addChildObject(xAxis);
		this.addChildObject(yAxis);
		this.addChildObject(zAxis);
	}
	
	public void applyObjTransformation(Matrix4f A) {
		// update vertex positions
		for (int i=0, j=0; i<meshData_.nVertices_; i++, j+=3) {
			//Vector4f v = new Vector4f(meshData_.verticesPos_[j], meshData_.verticesPos_[j+1], meshData_.verticesPos_[j+2], 1.f);
		}
	}

	@Override
	public void updateCenter() {
		// vertices' barycenter
//		if (meshData_!=null) {
//			float x = 0.f;
//			float y = 0.f;
//			float z = 0.f;
//			for (int i=0, j=0; i<meshData_.nVertices_; i++, j+=3) {
//				x += meshData_.verticesPos_[j];
//				y += meshData_.verticesPos_[j+1];
//				z += meshData_.verticesPos_[j+2];
//			}
//			objCenter_.set(0, x/meshData_.nVertices_);
//			objCenter_.set(1, y/meshData_.nVertices_);
//			objCenter_.set(2, z/meshData_.nVertices_);
//		}
		
		// center of bounding box
		if (meshData_!=null) {
			float xMin, yMin, zMin;
			float xMax, yMax, zMax;
			xMin = yMin = zMin = Float.MAX_VALUE;
			xMax = yMax = zMax = Float.MIN_VALUE;
			for (int i=0, j=0; i<meshData_.nVertices_; i++, j+=3) {
				xMin = Math.min(xMin, meshData_.verticesPos_[j]);
				yMin = Math.min(yMin, meshData_.verticesPos_[j+1]);
				zMin = Math.min(zMin, meshData_.verticesPos_[j+2]);
				xMax = Math.max(xMax, meshData_.verticesPos_[j]);
				yMax = Math.max(yMax, meshData_.verticesPos_[j+1]);
				zMax = Math.max(zMax, meshData_.verticesPos_[j+2]);
			}
			objCenter_.set((xMin+xMax)*0.5f, (yMin+yMax)*0.5f, (zMin+zMax)*0.5f);
		}
	}

	public Vector3f getVertex(int idx) {
		Vector3f pos = meshData_.getVertex(idx);
		Vector4f posW = getWorldTransformation().multiply(new Vector4f(pos, 1.f));
		return new Vector3f(posW.toArray(), 0);
	}
	
}
