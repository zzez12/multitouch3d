package zhl.Android.scenes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import zhl.Android.Multitouch.render.ZColor;
import zhl.Android.Multitouch.render.ZProjector;
import zhl.Android.Multitouch.render.ZView.EnumOperationMode;
import zhl.Android.math.Matrix4f;
import zhl.Android.math.Vector2f;
import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector4f;

abstract public class ZObject3D implements ZPickable3D, ZDrawable {
	public static final String LOG_TAG = ZObject3D.class.getSimpleName();
	
	private String name_ = ZObject3D.class.getSimpleName();
	protected Matrix4f transformation_ = Matrix4f.identityMatrix();
	protected Matrix4f tmpTransformation_ = Matrix4f.identityMatrix();
	
	protected FloatBuffer verticesBuffer_ = null;
	protected ShortBuffer indicesBuffer_ = null;
	protected FloatBuffer normalBuffer_ = null;
	protected int numOfVertices_ = -1;
	protected int numOfIndices_ = -1;
	protected float [] rgba_ = {1.0f, 1.0f, 1.0f, 1.0f};
	protected FloatBuffer colorBuffer_ = null;
	
	protected ZObject3D parentObject_ = null;
	private ArrayList<ZObject3D> childObjects_ = new ArrayList<ZObject3D>();
	private boolean visable_ = false;
	protected boolean bDirty_ = true;
	
	private ArrayList<Object> pickedObjs_ = new ArrayList<Object>();
	private ZAxis selectedAxis_ = null;
	private ZAxis[] selectedPlaneAxes_ = null;
	private ZPolygon3D selectedPlaneObj_ = null;
	private ZSnapPlane selectedSnapPlane_ = null;

	private boolean finishedAxisSelection_;
	private boolean isFocused_ = false;
	private boolean isSelected_ = false;
	
	// for picking
	protected Vector3f objCenter_ = new Vector3f();
	protected float boundingBallRadius_ = 0.f;
//	protected Vector3f maxCoord_ = new Vector3f();
//	protected Vector3f minCoord_ = new Vector3f();
	
	// bounding data
	
	
	abstract public void draw(GL10 gl);
	//abstract public void draw(GL10 gl, EnumOperationMode mode, Matrix4f transform);
	abstract public boolean pick(ZProjector proj, float x, float y);
	abstract public void prepareBuffers();
	
	ZObject3D() {
		this.parentObject_ = null;
	}
	
	public void addChildObject(ZObject3D obj) {
		this.getChildObjects().add(obj);
		obj.parentObject_ = this;
	}
	
	/////////////////////////////////////////////
	public void show() {
		this.setVisable(true);
		for (ZObject3D obj:getChildObjects()) {
			if (obj.isVisable()) obj.show();
		}
	}
	
	public void hide() {
		this.setVisable(false);
		for (ZObject3D obj:getChildObjects()) {
			obj.hide();
		}
	}
	//////////////////////////////////////////////
	
	public void endTransformation() {
		//this.transformation_ = this.transformation_.multiply(this.tmpTransformation_);
		this.transformation_ = this.tmpTransformation_.multiply(this.transformation_);
		this.tmpTransformation_ = Matrix4f.identityMatrix();
	}
	
	public void resetTransformation() {
		this.transformation_ = Matrix4f.identityMatrix();
		this.tmpTransformation_ = Matrix4f.identityMatrix();
	}
	
	public void applyTransformation(Matrix4f tran) {
		this.transformation_ = tran;
//		this.tmpTransformation_ = Matrix4f.identityMatrix();
//		for (ZObject3D obj:getChildObjects()) {
//			obj.applyTransformation(tran);
//		}
	}
	
	public Matrix4f getTransformation() {
		//Matrix4f ret = Matrix4f.identityMatrix();
		//return transformation_.multiply(getTmpTransformation()); 
		return getTmpTransformation().multiply(transformation_);
	}
	
	public Matrix4f getWorldTransformation() {
		if (this.parentObject_==null)
			return getTransformation();
		else
			return this.parentObject_.getTransformation().multiply(getTransformation());
	}
	
	public Matrix4f getCurrentTransformation() {
		if (this.parentObject_==null)
			return transformation_;
		else
			return this.parentObject_.transformation_.multiply(transformation_);
	}
	
	public void setVertices(float [] vertices) {
		 // a float is 4 bytes
		numOfVertices_ = vertices.length/3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer_ = vbb.asFloatBuffer();
		verticesBuffer_.put(vertices);
		verticesBuffer_.position(0);
	}
	
	public void setIndices(short [] indices) {
		 // a short is 2 bytes
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer_ = ibb.asShortBuffer();
		indicesBuffer_.put(indices);
		indicesBuffer_.position(0);
		numOfIndices_ = indices.length;
	}
	
	public void setColor(float r, float g, float b, float a) {
		rgba_[0] = r;
		rgba_[1] = g;
		rgba_[2] = b;
		rgba_[2] = a;
	}
	
	public void setColor(float [] colors) {
		// float has 4 bytes.
		if (colors==null) return;
		 ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		 cbb.order(ByteOrder.nativeOrder());
		 colorBuffer_ = cbb.asFloatBuffer();
		 colorBuffer_.put(colors);
		 colorBuffer_.position(0);
	}
	
	public void setNormal(float [] normals) {
		ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
		nbb.order(ByteOrder.nativeOrder());
		normalBuffer_ = nbb.asFloatBuffer();
		normalBuffer_.put(normals);
		normalBuffer_.position(0);
	}

	public void updateObject(GL10 gl) {
		if (verticesBuffer_!=null) {
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer_);
		}
		if (colorBuffer_!=null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer_);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			//gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glColor4f(rgba_[0], rgba_[1], rgba_[2], rgba_[3]);
		}
		if (normalBuffer_!=null) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer_);
		}
	}
	
	public void makeDirty() {
		bDirty_ = true;
	}
	public void makeUnDirty() {
		bDirty_ = false;
	}
	
	public boolean isDirty() {
		return bDirty_;
	}
	
	public void showAxes() {
		for (ZObject3D obj : this.getChildObjects()) {
			if (obj instanceof ZAxis) {
				obj.show();
			}
		}
	}
	public void hideAxes() {
		for (ZObject3D obj : this.getChildObjects()) {
			if (obj instanceof ZAxis) {
				obj.hide();
			}
		}
	}
	
	public void deselectAllSnapPlanes() {
		// TODO
		for (ZObject3D obj:getChildObjects()) {
			if (obj instanceof ZSnapPlane) {
				obj.hide();
			}
		}
	}

	public ZAxis pickAxis(ZProjector proj, Vector2f touchOrientation) {
		ZAxis retAxis = null;
		Vector2f orientation = touchOrientation.times(1.f/touchOrientation.length());
		float maxError = Float.MIN_VALUE;
		for (ZObject3D obj:getChildObjects()) {
			if (obj instanceof ZAxis) {
				ZAxis axis = (ZAxis)obj;
				// compute projected axis
				Vector3f c = axis.getOri();
				Vector3f v = c.plus(axis.getDir());
				Vector3f proj_c = proj.project(c);
				Vector3f proj_v = proj.project(v);
				Vector2f projectedAxis2D = new Vector2f(proj_v.x_ - proj_c.x_, proj_v.y_ - proj_v.y_);
				
				// store projected coordinates to axis object
				axis.setProjectedAxis(projectedAxis2D);
				axis.setProjectionScale(projectedAxis2D.length());
				
				// skip axis with short length
				//float lengthRatio = projectedAxis2D.length();
				//if (lengthRatio < 0.05f) continue;
				
				// compute error
				float error = Math.abs(projectedAxis2D.normalize().dot(orientation));
				// adjust error value (lower the value if is not object axis
				if (axis.getType() !=  ZAxis.AxisType.ObjectAxis) {
					error = error * error;
				}
				// get the best one
				if (error>maxError) {
					maxError = error;
					retAxis = axis;
				}
			}
		}
		return retAxis;
	}
	public ZAxis[] getSelectedPlaneAxes() {
		return selectedPlaneAxes_;
	}
	public void setSelectedPlaneAxes(ZAxis[] selectedPlaneAxes_) {
		this.selectedPlaneAxes_ = selectedPlaneAxes_;
		this.buildSelectedPlane();
	}
	public Vector3f getObjCenter() {
		return objCenter_;
	}
	public void setObjCenter(Vector3f center_) {
		this.objCenter_ = center_;
	}
	public ZAxis getSelectedAxis() {
		return selectedAxis_;
	}
	public void setSelectedAxis(ZAxis selectedAxis_) {
		this.selectedAxis_ = selectedAxis_;
	}
	public boolean isFinishedAxisSelection() {
		return finishedAxisSelection_;
	}
	public void setFinishedAxisSelection(boolean finishedAxisSelection_) {
		this.finishedAxisSelection_ = finishedAxisSelection_;
	}
	public boolean isVisable() {
		return visable_;
	}
	public void setVisable(boolean visable_) {
		this.visable_ = visable_;
	}
	public ArrayList<Object> getPickedObjs() {
		return pickedObjs_;
	}
	public void setPickedObjs(ArrayList<Object> pickedObjs_) {
		this.pickedObjs_ = pickedObjs_;
	}
	public ArrayList<ZObject3D> getChildObjects() {
		return childObjects_;
	}
	public void setChildObjects(ArrayList<ZObject3D> childObjects_) {
		this.childObjects_ = childObjects_;
	}
	public boolean isFocused() {
		return isFocused_;
	}
	public void setFocused(boolean isFocused_) {
		this.isFocused_ = isFocused_;
	}
	public boolean isSelected() {
		return isSelected_;
	}
	public void setSelected(boolean isSelected_) {
		this.isSelected_ = isSelected_;
	}
	
	public void removeNonObjectAxes() {
		List<ZAxis> removeList = new ArrayList<ZAxis>();
		for (ZObject3D obj:this.getChildObjects()) {
			if (obj instanceof ZAxis) {
				ZAxis axis = (ZAxis)obj;
				if (axis.getType()!=ZAxis.AxisType.ObjectAxis) {
					removeList.add(axis);
				}
			}
		}
		childObjects_.removeAll(removeList);
	}
	
	public void addGlobalAxes() {
		float [] blue = ZColor.fromRGBA(1.f, 0.f, 0.f, 100.f/255.f);
		ZAxis a1 = new ZAxis(this.getObjCenter(), new Vector3f(0.5f, 0.f, 0.f), blue, ZAxis.AxisType.GlobalAxis);
		ZAxis a2 = new ZAxis(this.getObjCenter(), new Vector3f(0.f, 0.5f, 0.f), blue, ZAxis.AxisType.GlobalAxis);
		ZAxis a3 = new ZAxis(this.getObjCenter(), new Vector3f(0.f, 0.f, 0.5f), blue, ZAxis.AxisType.GlobalAxis);
		this.addChildObject(a1);
		this.addChildObject(a2);
		this.addChildObject(a3);
	}
	
	public void addReferenceAxes(ZObject3D ref, boolean centerPivot) {
		// TODO
	}

	public void addScreenAxes(ZProjector proj){
		Vector3f c = proj.project(this.getObjCenter());
		Vector3f v1 = c.plus(new Vector3f(1.f, 0.f, 0.f));
		Vector3f v2 = c.plus(new Vector3f(0.f, 1.f, 0.f));
		Vector3f u1 = proj.unProject(v1).minus(this.getObjCenter());
		Vector3f u2 = proj.unProject(v2).minus(this.getObjCenter());
		u1 = u1.normalize().times(0.5f);
		u2 = u2.normalize().times(0.5f);
		float [] green = ZColor.fromRGBA(0.f, 1.f, 0.f, 100.f/255.f);
		ZAxis a1 = new ZAxis(this.getObjCenter(), u1, green, ZAxis.AxisType.ScreenAxis);
		ZAxis a2 = new ZAxis(this.getObjCenter(), u2, green, ZAxis.AxisType.ScreenAxis);
		this.addChildObject(a1);
		this.addChildObject(a2);
	}
	public String getName() {
		return name_;
	}
	public void setName(String name_) {
		this.name_ = name_;
	}
	
	public String getInfos() {
		String str = "";
		str += "++ " + getName() + ", ";
		str += "Visable: " + isVisable() + ", ";
		str += "Focused: " + isFocused() + " --! ";
		for (ZObject3D obj : getChildObjects()) {
			str += obj.getInfos();
		}
		return str;
	}
	public ZSnapPlane getSelectedSnapPlane() {
		return selectedSnapPlane_;
	}
	public void setSelectedSnapPlane(ZSnapPlane selectedSnapPlane_) {
		for (ZObject3D obj:getChildObjects()) {
			if (obj instanceof ZSnapPlane) {
				ZSnapPlane snapPlane = (ZSnapPlane)obj;
				snapPlane.hide();
			}
		}
		this.selectedSnapPlane_ = selectedSnapPlane_;
		this.selectedSnapPlane_.show();
	}
	
	public void updateCenter() {
		// do nothing by default
	}
	public Matrix4f getTmpTransformation() {
		return tmpTransformation_;
	}
	public void setTmpTransformation(Matrix4f tmpTransformation_) {
		this.tmpTransformation_ = new Matrix4f(tmpTransformation_);
	}
	
	private void buildSelectedPlane() {
		if (getSelectedPlaneAxes()==null) return;
		
		Vector3f f1 = this.selectedPlaneAxes_[0].getDir().times(2.f);
		Vector3f f2 = this.selectedPlaneAxes_[1].getDir().times(2.f);
		Vector3f c = this.getObjCenter();
		Vector3f[] vs = new Vector3f[4];
		vs[0] = new Vector3f(c.plus(f1).plus(f2));
		vs[1] = new Vector3f(c.minus(f1).plus(f2));
		vs[2] = new Vector3f(c.minus(f1).minus(f2));
		vs[3] = new Vector3f(c.plus(f1).minus(f2));
		this.selectedPlaneObj_ = new ZPolygon3D(vs);
	}
	
	protected void drawSelectedPlane(GL10 gl) {
		if (this.selectedPlaneObj_==null || this.getSelectedPlaneAxes()==null) return;
		gl.glDisable(GL10.GL_CULL_FACE);
		selectedPlaneObj_.draw(gl);
		gl.glEnable(GL10.GL_CULL_FACE);
	}
	
	/*
	 * This function helps to change from the input original vector to the current changed vector
	 */
	public Vector3f getCurrentVector(Vector3f posOri, boolean bWithTmpTrans, boolean bVector) {
		float w = 1.f;
		if (bVector) w = 0.f;
		if (bWithTmpTrans) {
			Vector4f transP = getWorldTransformation().multiply(new Vector4f(posOri, w));
			return new Vector3f(transP.toArray(), 0);	
		}
		else {
			Vector4f transP = getCurrentTransformation().multiply(new Vector4f(posOri, w));
			return new Vector3f(transP.toArray(), 0);	

		}
	}
	public void reset() {
		// TODO Auto-generated method stub
		this.transformation_ = Matrix4f.identityMatrix();
		this.tmpTransformation_ = Matrix4f.identityMatrix();
	}
	
}
