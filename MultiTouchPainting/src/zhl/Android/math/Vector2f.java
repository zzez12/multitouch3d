package zhl.Android.math;

public class Vector2f {
	public static final Vector2f MinValue = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vector2f MaxValue = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
	
	public float x_, y_;
	

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (this==o) return true;
		if (!(o instanceof Vector2f)) return false;
		final Vector2f v = (Vector2f)o;
		if (v.x_==this.x_ && v.y_==this.y_) return true;
		return false;
		//return super.equals(o);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//return super.toString();
		return "("+x_+","+y_+")";
	}

	public Vector2f() {
		x_ = 0f;
		y_ = 0f;
	}
	
	public Vector2f(float x, float y) {
		x_ = x;
		y_ = y;
	}
	public Vector2f(float[] arr, int index) {
		x_ = arr[index];
		y_ = arr[index+1];
	}
	
	public float get(int index) {
		if (index==0) return x_;
		else if (index==1) return y_;
		else throw new ArrayIndexOutOfBoundsException(); 
	}
	
	public float dot(Vector2f v) {
		return x_*v.x_+y_*v.y_;
	}
	
	public float length() {
		return (float) Math.sqrt(x_*x_+y_*y_);
	}
	
	public Vector2f normalize() {
		float le = length();
		return this.divide(le);
	}
	
	public Vector2f plus(Vector2f v) {
		Vector2f ret = new Vector2f();
		ret.x_ = v.x_+x_;
		ret.y_ = v.y_+y_;
		return ret;
	}
	public Vector2f minus(Vector2f v) {
		Vector2f ret = new Vector2f();
		ret.x_ = x_-v.x_;
		ret.y_ = y_-v.y_;
		return ret;
	}
	public Vector2f times(float f) {
		Vector2f ret = new Vector2f();
		ret.x_ = x_*f;
		ret.y_ = y_*f;
		return ret;
	}
	public Vector2f divide(float f) {
		Vector2f ret = new Vector2f();
		ret.x_ = x_/f;
		ret.y_ = y_/f;
		return ret;
	}
	
	public static Vector2f plus(Vector2f v0, Vector2f v1) {
		return new Vector2f(v0.x_+v1.x_, v0.y_+v1.y_);
	}
	public static Vector2f minus(Vector2f v0, Vector2f v1) {
		return new Vector2f(v0.x_-v1.x_, v0.y_-v1.y_);
	}
	public static Vector2f times(Vector2f v, float f) {
		return new Vector2f(v.x_*f, v.y_*f);
	}
	public static Vector2f times(float f, Vector2f v) {
		return new Vector2f(v.x_*f, v.y_*f);
	}
	public static Vector2f divide(Vector2f v, float f) {
		return new Vector2f(v.x_/f, v.y_/f);
	}
}
