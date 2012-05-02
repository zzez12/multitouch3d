package zhl.Android.scenes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

abstract public class ZObject2D implements ZDrawable{
	public static final String LOG_TAG = ZObject2D.class.getSimpleName();
	
	private String name_ = ZObject2D.class.getSimpleName();
	
	protected FloatBuffer verticesBuffer_ = null;
	protected ShortBuffer indicesBuffer_ = null;
	protected int numOfVertices_ = -1;
	protected int numOfIndices_ = -1;
	protected float [] rgba_ = {1.f, 1.f, 1.f, 1.f};
	protected FloatBuffer colorBuffer_= null;
	
	protected ZObject2D parentObject_ = null;
	private ArrayList<ZObject2D> childObjects_ = new ArrayList<ZObject2D>();
	private boolean visable_ = false;
	protected boolean bDirty_ = true;
	
	ZObject2D() {
		this.parentObject_ = null;
	}
	
	public void addChildObject(ZObject2D obj) {
		this.getChildObjects().add(obj);
		obj.parentObject_ = this;
	}
	
	public void show() {
		this.setVisable(true);
		for (ZObject2D obj : getChildObjects()) {
			if (obj.isVisable()) obj.show();
		}
	}
	
	public void hide() {
		this.setVisable(false);
		for (ZObject2D obj : getChildObjects()) {
			obj.hide();
		}
	}

	public ArrayList<ZObject2D> getChildObjects() {
		return childObjects_;
	}

	public void setChildObjects(ArrayList<ZObject2D> childObjects_) {
		this.childObjects_ = childObjects_;
	}

	public boolean isVisable() {
		return visable_;
	}

	public void setVisable(boolean visable_) {
		this.visable_ = visable_;
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
			gl.glColor4f(rgba_[0], rgba_[1], rgba_[2], rgba_[3]);
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
	
	public String getName() {
		return name_;
	}
	public void setName(String name_) {
		this.name_ = name_;
	}
}
