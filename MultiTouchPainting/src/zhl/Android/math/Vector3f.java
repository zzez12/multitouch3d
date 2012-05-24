package zhl.Android.math;

public class Vector3f {
	public static final Vector3f MaxValue = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vector3f MinValue = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
	
	public float x_, y_, z_;
	

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (this==o) return true;
		if (!(o instanceof Vector3f)) return false;
		final Vector3f v = (Vector3f)o;
		if (v.x_==this.x_ && v.y_==this.y_ && v.z_==this.z_) return true;
		return false;
		//return super.equals(o);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//return super.toString();
		return "("+x_+","+y_+","+z_+")";
	}

	public Vector3f() {
		x_ = 0.0f;
		y_ = 0.0f;
		z_ = 0.0f;
	}
	
	public Vector3f(float x, float y, float z) {
		x_ = x;
		y_ = y;
		z_ = z;
	}
	public Vector3f(float[] arr, int index) {
		x_ = arr[index];
		y_ = arr[index+1];
		z_ = arr[index+2];
	}
	public Vector3f(Vector3f v) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = v.z_;
	}
	
	public float get(int index) {
		if (index==0) return x_;
		else if (index==1) return y_;
		else if (index==2) return z_;
		else throw new ArrayIndexOutOfBoundsException(); 
	}
	
	public void set(int index, float f) {
		if (index==0) x_=f;
		else if (index==1) y_=f;
		else if (index==2) z_=f;
		else throw new ArrayIndexOutOfBoundsException(); 
	}
	
	public void set(float x, float y, float z) {
		x_ = x;
		y_ = y;
		z_ = z;
	}
	
	public void set(Vector3f v) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = v.z_;
	}
	
	public float dot(Vector3f v) {
		return x_*v.x_+y_*v.y_+z_*v.z_;
	}
	
	public float length() {
		return (float) Math.sqrt(x_*x_+y_*y_+z_*z_);
	}
	
	public Vector3f normalize() {
		float le = length();
		//return this.divide(le);
		return this.times(1.f/le);
	}
	
	public Vector3f plus(Vector3f v) {
		Vector3f ret = new Vector3f();
		ret.x_ = v.x_+x_;
		ret.y_ = v.y_+y_;
		ret.z_ = v.z_+z_;
		return ret;
	}
	public Vector3f minus(Vector3f v) {
		Vector3f ret = new Vector3f();
		ret.x_ = x_-v.x_;
		ret.y_ = y_-v.y_;
		ret.z_ = z_-v.z_;
		return ret;
	}
	public Vector3f times(float f) {
		Vector3f ret = new Vector3f();
		ret.x_ = x_*f;
		ret.y_ = y_*f;
		ret.z_ = z_*f;
		return ret;
	}
	public Vector3f divide(float f) {
		Vector3f ret = new Vector3f();
		ret.x_ = x_/f;
		ret.y_ = y_/f;
		ret.z_ = z_/f;
		return ret;
	}
	public Vector3f cross(Vector3f v) {
		return new Vector3f(
				y_*v.z_ - v.y_*z_,
				z_*v.x_ - v.z_*x_,
				x_*v.y_ - v.x_*y_);
	}
	
	public Matrix3f outerCross(Vector3f v) {
		Matrix3f m = new Matrix3f();
		m.set(0, 0, x_*v.x_);
		m.set(0, 1, x_*v.y_);
		m.set(0, 2, x_*v.z_);
		m.set(1, 0, y_*v.x_);
		m.set(1, 1, y_*v.y_);
		m.set(1, 2, y_*v.z_);
		m.set(2, 0, z_*v.x_);
		m.set(2, 1, z_*v.y_);
		m.set(2, 2, z_*v.z_);
		return m;
	}
	
	public Vector3f max(Vector3f p) {
		return new Vector3f(Math.max(x_, p.x_), Math.max(y_, p.y_), Math.max(z_, p.z_));
	}
	
	public Vector3f min(Vector3f p) {
		return new Vector3f(Math.min(x_, p.x_), Math.min(y_, p.y_), Math.min(z_, p.z_));		
	}
	
	public static Vector3f plus(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0.x_+v1.x_, v0.y_+v1.y_, v0.z_+v1.z_);
	}
	public static Vector3f minus(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0.x_-v1.x_, v0.y_-v1.y_, v0.z_-v1.z_);
	}
	public static Vector3f times(Vector3f v, float f) {
		return new Vector3f(v.x_*f, v.y_*f, v.z_*f);
	}
	public static Vector3f times(float f, Vector3f v) {
		return new Vector3f(v.x_*f, v.y_*f, v.z_*f);
	}
	public static Vector3f divide(Vector3f v, float f) {
		return new Vector3f(v.x_/f, v.y_/f, v.z_/f);
	}
	
	public static Vector3f max(Vector3f p, Vector3f q) {
		return p.max(q);
	}
	
	public static Vector3f min(Vector3f p, Vector3f q) {
		return p.min(q);
	}
}
