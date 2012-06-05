package zhl.Android.math;

public class Trackball {
	public enum MotionType { None, Rotation, Pan, Scale, Scale2 };
	
	private MotionType type_ = MotionType.None;
	private Vector2f stPt_ = new Vector2f(), edPt_ = new Vector2f();
	private Vector3f stVec_ = new Vector3f(), edVec_ = new Vector3f();
	private Vector4f quat_ = new Vector4f();
	
	private float w_, h_;
	private float adjustWidth_, adjustHeight_;	
	
	private float scale_ = 1.f;

	public MotionType getType() {
		return type_;
	}
	public void setType(MotionType type_) {
		this.type_ = type_;
	}

	public Vector2f size() {
		return new Vector2f(w_, h_);
	}
	public void setSize(float w, float h) {
		w_ = w;
		h_ = h;
	}
	public void setSize(Vector2f s) {
		w_ = s.x_;
		h_ = s.y_;
	}
	
	// constructor
	public Trackball(float w, float h) {
		setBounds(w, h);
	}
	
	
	// public functions
	public void setBounds(float w, float h) {
		float b = (w<h) ? w : h;
		this.w_ = w/2.f;
		this.h_ = h/2.f;
		this.adjustWidth_ = 1.f/((b-1.f)*0.5f);
		this.adjustHeight_ = 1.f/((b-1.f)*0.5f);
	}
	
	public void click(Vector2f pt, MotionType type) {
		this.stPt_ = pt;
		this.stVec_ = mapToSphere(pt);
		this.type_ = type;
	}
	
	public void drag(Vector2f pt) {
		this.edPt_ = pt;
		this.edVec_ = mapToSphere(pt);
		float epsilon = (float) 1.0e-5;
		Vector3f prep = stVec_.cross(edVec_);
		if (prep.length()>epsilon)
			this.quat_ = new Vector4f(prep, stVec_.dot(edVec_));
		else
			this.quat_ = new Vector4f();
	}
	
	public void scale(float scale) {
		//this.scale_ = 1.f + scale * this.adjustWidth_;
		this.scale_ *= scale;
		this.type_ = MotionType.Scale2;
	}
	
	public void end() {
		quat_ = new Vector4f();
		type_ = MotionType.None;
		scale_ = 1.f;
	}
	
	public Matrix4f getMatrix() {
		if (type_ == MotionType.Rotation)
			return quatToMatrix4f(quat_);
		if (type_ == MotionType.Scale) {
			Matrix4f m = Matrix4f.identityMatrix();
			float f = 1.f + (edPt_.x_ - stPt_.x_) * adjustWidth_;
			m.set(0, 0, f);
			m.set(1, 1, f);
			m.set(2, 2, f);
			return m;
		}
		if (type_ == MotionType.Pan) {
			Matrix4f m = Matrix4f.identityMatrix();
			m.set(0, 3, edPt_.x_ - stPt_.x_);
			m.set(1, 3, edPt_.y_ - stPt_.y_);
			return m;
		}
		if (type_ == MotionType.Scale2) {
			Matrix4f m = Matrix4f.identityMatrix();
			m.set(0, 0, scale_);
			m.set(1, 1, scale_);
			m.set(2, 2, scale_);
			return m;
		}
		return Matrix4f.identityMatrix();
	}

	public float getScale() {
		if (type_ == MotionType.Scale) {
			return 1.f + (edPt_.x_-stPt_.x_)*adjustWidth_;
		} 
		else return 1.f;
	}
	
	// private functions
	private Vector3f mapToSphere(Vector2f pt) {
		Vector2f v = new Vector2f();
		v.x_ = (w_ - pt.x_) * adjustWidth_;
		v.y_ = (h_ - pt.y_) * adjustHeight_;

		double lenSq = v.dot(v);
		if (lenSq > 1.f)
		{
			v = v.normalize();
			return new Vector3f(v.x_, v.y_, 0);
		}
		else
			return new Vector3f(v.x_, v.y_, (float)Math.sqrt(1.f - lenSq));
	}
	
	private Matrix3f quatToMatrix3f(Vector4f q) {
        float n = q.dot(q);
        float s = (n > 0.0f) ? (2.f / n) : 0.0f;

        float xs, ys, zs;
        float wx, wy, wz;
        float xx, xy, xz;
        float yy, yz, zz;
        xs = q.x_ * s; ys = q.y_ * s; zs = q.z_ * s;
        wx = q.w_ * xs; wy = q.w_ * ys; wz = q.w_ * zs;
        xx = q.x_ * xs; xy = q.x_ * ys; xz = q.x_ * zs;
        yy = q.y_ * ys; yz = q.y_ * zs; zz = q.z_ * zs;

        Matrix3f m = new Matrix3f();
        m.set(0, 0, 1.f - (yy + zz)*2.f);
        m.set(1, 0, (xy - wz)*2.f);
        m.set(2, 0, (xz + wy)*2.f);
        m.set(0, 1, (xy + wz)*2.f);
        m.set(1, 1, 1.f - (xx + zz)*2.f);
        m.set(2, 1, (yz - wx)*2.f);
        m.set(0, 2, (xz - wy)*2.f);
        m.set(1, 2, (yz + wx)*2.f);
        m.set(2, 2, 1.f - (xx + yy)*2.f);
        return m;
	}
	
	private Matrix4f quatToMatrix4f(Vector4f q) {
		float n = q.dot(q);
		float s = (n > 0.f) ? (2.f / n) : 0.0f;

		float xs, ys, zs;
		float wx, wy, wz;
		float xx, xy, xz;
		float yy, yz, zz;
		xs = q.x_ * s; ys = q.y_ * s; zs = q.z_ * s;
		wx = q.w_ * xs; wy = q.w_ * ys; wz = q.w_ * zs;
		xx = q.x_ * xs; xy = q.x_ * ys; xz = q.x_ * zs;
		yy = q.y_ * ys; yz = q.y_ * zs; zz = q.z_ * zs;

		Matrix4f m = new Matrix4f();
		m.set(0, 0, 1.f - (yy + zz));
		m.set(1, 0, xy - wz);
		m.set(2, 0, xz + wy);		
		m.set(0, 1, xy + wz);	
		m.set(1, 1, 1.f - (xx + zz));
		m.set(2, 1, yz - wx);		
		m.set(0, 2, xz - wy);	
		m.set(1, 2, yz + wx);
		m.set(2, 2, 1.f - (xx + yy));
		m.set(3, 3, 1.f);
		return m;
	}
}
