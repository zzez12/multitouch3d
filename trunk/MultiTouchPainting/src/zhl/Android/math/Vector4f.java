package zhl.Android.math;

public class Vector4f {
	//public static final Vector4f MinValue = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	//public static final Vector4f MaxValue = new Vector4f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	
	public float x_, y_, z_, w_;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//return super.toString();
		return "("+x_+","+y_+","+z_+","+w_+")";
	}
	
	public float [] toArray() {
		return new float[]{x_, y_, z_, w_};
	}

	public Vector4f() {
		x_ = 0.0f;
		y_ = 0.0f;
		z_ = 0.0f;
		w_ = 0.0f;
	}
	
	public Vector4f(float x, float y, float z, float w) {
		x_ = x;
		y_ = y;
		z_ = z;
		w_ = w;
	}
	public Vector4f(float[] arr, int index) {
		x_ = arr[index];
		y_ = arr[index+1];
		z_ = arr[index+2];
		w_ = arr[index+3];
	}
	public Vector4f(Vector2f v) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = 0.0f;
		w_ = 0.0f;
	}
	public Vector4f(Vector2f v, float z, float w) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = z;
		w_ = w;
	}
	public Vector4f(Vector3f v) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = v.z_;
		w_ = 0.0f;
	}
	public Vector4f(Vector3f v, float w) {
		x_ = v.x_;
		y_ = v.y_;
		z_ = v.z_;
		w_ = w;
	}
	
	public float get(int index) {
		if (index==0) return x_;
		else if (index==1) return y_;
		else if (index==2) return z_;
		else if (index==3) return w_;
		else throw new ArrayIndexOutOfBoundsException(); 
	}
	
	public void set(int index, float f) {
		if (index==0) x_=f;
		else if (index==1) y_=f;
		else if (index==2) z_=f;
		else if (index==3) w_=f;
		else throw new ArrayIndexOutOfBoundsException(); 
	}
	
	public float dot(Vector4f v) {
		return x_*v.x_+y_*v.y_+z_*v.z_+w_*v.w_;
	}
	
	public float length() {
		return (float) Math.sqrt(x_*x_+y_*y_+z_*z_+w_*w_);
	}
	
	public Vector4f normalize() {
		float le = length();
		return this.divide(le);
	}
	
	public Vector4f plus(Vector4f v) {
		x_ = v.x_+x_;
		y_ = v.y_+y_;
		z_ = v.z_+z_;
		return this;
	}
	public Vector4f minus(Vector4f v) {
		x_ = x_-v.x_;
		y_ = y_-v.y_;
		z_ = z_-v.z_;
		return this;
	}
	public Vector4f times(float f) {
		x_ = x_*f;
		y_ = y_*f;
		z_ = z_*f;
		w_ = w_*f;
		return this;
	}
	public Vector4f divide(float f) {
		x_ = x_/f;
		y_ = y_/f;
		z_ = z_/f;
		w_ = w_/f;
		return this;
	}
	
	public static Vector4f plus(Vector4f v0, Vector4f v1) {
		return new Vector4f(v0.x_+v1.x_, v0.y_+v1.y_, v0.z_+v1.z_, v0.w_+v1.w_);
	}
	public static Vector4f minus(Vector4f v0, Vector4f v1) {
		return new Vector4f(v0.x_-v1.x_, v0.y_-v1.y_, v0.z_-v1.z_, v0.w_-v1.w_);
	}
	public static Vector4f times(Vector4f v, float f) {
		return new Vector4f(v.x_*f, v.y_*f, v.z_*f, v.w_*f);
	}
	public static Vector4f times(float f, Vector4f v) {
		return new Vector4f(v.x_*f, v.y_*f, v.z_*f, v.w_*f);
	}
	public static Vector4f divide(Vector4f v, float f) {
		return new Vector4f(v.x_/f, v.y_/f, v.z_/f, v.w_*f);
	}
}
