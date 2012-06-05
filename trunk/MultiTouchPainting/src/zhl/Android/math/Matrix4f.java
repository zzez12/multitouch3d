package zhl.Android.math;

public class Matrix4f {
	public static final int len_ = 16;
	public static final int row_size_ = 4;
	public static final int col_size_ = 4;
	private float [] e_ = new float[len_];
	
	public static Matrix4f identityMatrix() {
		// TODO Auto-generated method stub
		Matrix4f m = new Matrix4f();
		m.set(0, 1.f);
		m.set(5, 1.f);
		m.set(10, 1.f);
		m.set(15, 1.f);
		return m;
	}
	
	public static Matrix4f translationMatrix(Vector3f p) {
		Matrix4f m = new Matrix4f();
		m.set(0, 1.f);
		m.set(5, 1.f);
		m.set(10, 1.f);
		m.set(15, 1.f);
		m.set(3, p.x_);
		m.set(7, p.y_);
		m.set(11, p.z_);
		return m;
	}
	
	public static Matrix4f rotationMatrix(Vector3f axis, float theta) {
		Matrix4f m = new Matrix4f();
		float cosA = (float)Math.cos(theta);
		float sinA = (float)Math.sin(theta);
		m.set(0, cosA+(1-cosA)*axis.x_*axis.x_);
		m.set(1, (1-cosA)*axis.x_*axis.y_-sinA*axis.z_);
		m.set(2, (1-cosA)*axis.x_*axis.z_+sinA*axis.y_);
		m.set(4, (1-cosA)*axis.x_*axis.y_+sinA*axis.z_);
		m.set(5, cosA+(1-cosA)*axis.y_*axis.y_);
		m.set(6, (1-cosA)*axis.y_*axis.z_-sinA*axis.x_);
		m.set(8, (1-cosA)*axis.x_*axis.z_-sinA*axis.y_);
		m.set(9, (1-cosA)*axis.y_*axis.z_+sinA*axis.x_); 
		m.set(10, cosA+(1-cosA)*axis.z_*axis.z_);
		m.set(15, 1.f);
		return m;
	}
	
	public static Matrix4f rotationMatrix(Vector3f center, Vector3f axis, float theta) {
		Matrix4f m = translationMatrix(center).multiply(rotationMatrix(axis, theta)).multiply(translationMatrix(center.times(-1.f)));
		return m;
	}
	
	public static Matrix4f scalingMatrix(Vector3f center, float scale) {
		Matrix4f m = translationMatrix(center).multiply(identityMatrix().times(scale)).translationMatrix(center.times(-1.f));
		return m;
	}
	
	public Matrix4f() {
		for (int i=0; i<len_; i++) e_[i] = 0.f;
	}
	
	public Matrix4f(float [] arr) {
		for (int i=0; i<len_; i++) e_[i] = arr[i];
	}
	public Matrix4f(float[][] arr) {
		for (int x=0; x<row_size_; x++)
			for (int y=0; y<col_size_; y++)
				set(x, y, arr[x][y]);
	}
	public Matrix4f(Matrix3f m) {
		for (int x=0; x<Matrix3f.row_size_; x++)
			for (int y=0; y<Matrix3f.col_size_; y++)
				set(x, y, m.get(x, y));
	}
	public Matrix4f(Vector4f v1, Vector4f v2, Vector4f v3, Vector4f v4) {
		for (int i=0; i<row_size_; i++) {
			set(i, 0, v1.get(i));
			set(i, 1, v2.get(i));
			set(i, 2, v3.get(i));
			set(i, 3, v4.get(i));
		}
	}
	public Matrix4f(Matrix4f m) {
		for (int i=0; i<row_size_; i++)
			for (int j=0; j<col_size_;j ++) {
				set(i,j,m.get(i, j));
			}
	}
	
	public Matrix4f multiply(Matrix4f m) {
		Matrix4f ret = new Matrix4f();
		for (int x=0; x<row_size_; x++)
			for (int y=0; y<col_size_; y++) {
				float f = 0.f;
				for (int k=0; k<col_size_; k++) {
					f += get(x, k)*m.get(k, y);
				}
				ret.set(x, y, f);
			}
		return ret;
	}
	
	public Vector4f multiply(Vector4f v) {
		Vector4f ret = new Vector4f();
		ret.x_ = e_[0]*v.x_ + e_[1]*v.y_ + e_[2]*v.z_ + e_[3]*v.w_;
		ret.y_ = e_[4]*v.x_ + e_[5]*v.y_ + e_[6]*v.z_ + e_[7]*v.w_;
		ret.z_ = e_[8]*v.x_ + e_[9]*v.y_ + e_[10]*v.z_ + e_[11]*v.w_;
		ret.w_ = e_[12]*v.x_ + e_[13]*v.y_ + e_[14]*v.z_ + e_[15]*v.w_;
		return ret;
	}
	
	public Vector3f multiply(Vector3f v, boolean bVector) {
		Vector4f ret4f = this.multiply(new Vector4f(v, bVector?0.f:1.f));
		return new Vector3f(ret4f.toArray(), 0);
	}
	
	public Matrix4f preMultiply(Matrix4f m) {
		Matrix4f ret = new Matrix4f();
		for (int x=0; x<row_size_; x++)
			for (int y=0; y<col_size_; y++) {
				float f = 0.f;
				for (int k=0; k<col_size_; k++) {
					//f += get(x, k)*m.get(k, y);
					f += m.get(x, k)*get(k, y);
				}
				ret.set(x, y, f);
			}
		return ret;
	}
	
	public Matrix4f times(float f) {
		Matrix4f ret = new Matrix4f();
		for (int i=0; i<len_; i++)
			ret.set(i, get(i)*f);
		return ret;
	}
		
	public float determinate() {
		float a11 = e_[0];
		float a12 = e_[1];
		float a13 = e_[2];
		float a14 = e_[3];
		float a21 = e_[4];
		float a22 = e_[5];
		float a23 = e_[6];
		float a24 = e_[7];
		float a31 = e_[8];
		float a32 = e_[9];
		float a33 = e_[10];
		float a34 = e_[11];
		float a41 = e_[12];
		float a42 = e_[13];
		float a43 = e_[14];
		float a44 = e_[15];
		
		float ret = a11*a22*a33*a44 + a11*a23*a34*a42 + a11*a24*a32*a43
				  + a12*a21*a34*a43 + a12*a23*a31*a44 + a12*a24*a33*a41
				  + a13*a21*a32*a44 + a13*a22*a34*a41 + a13*a24*a31*a42
				  + a14*a21*a33*a42 + a13*a22*a31*a43 + a14*a23*a32*a41
				  - a11*a22*a34*a43 - a11*a23*a32*a44 - a11*a24*a33*a42
				  - a12*a21*a33*a44 - a12*a23*a34*a41 - a12*a24*a31*a43
				  - a13*a21*a34*a42 - a13*a22*a31*a44 - a13*a24*a32*a41
				  - a14*a21*a32*a43 - a14*a22*a33*a41 - a14*a23*a31*a42;
		return ret;
	}
	
	public Matrix4f inverse() {
		float det = determinate();
		if (det==0.f) throw new ArithmeticException();
		
		float a11 = e_[0];
		float a12 = e_[1];
		float a13 = e_[2];
		float a14 = e_[3];
		float a21 = e_[4];
		float a22 = e_[5];
		float a23 = e_[6];
		float a24 = e_[7];
		float a31 = e_[8];
		float a32 = e_[9];
		float a33 = e_[10];
		float a34 = e_[11];
		float a41 = e_[12];
		float a42 = e_[13];
		float a43 = e_[14];
		float a44 = e_[15];

		Matrix4f ret = new Matrix4f();
		ret.set(0, 0, a22*a33*a44 + a23*a34*a42 + a24*a32*a43 - a22*a34*a43 - a23*a32*a44 - a24*a33*a42);
		ret.set(0, 1, a12*a34*a43 + a13*a32*a44 + a14*a33*a42 - a12*a33*a44 - a13*a34*a42 - a14*a32*a43);
		ret.set(0, 2, a12*a23*a44 + a13*a24*a42 + a14*a22*a43 - a12*a24*a43 - a13*a22*a44 - a14*a23*a42);
		ret.set(0, 3, a12*a24*a33 + a13*a22*a34 + a14*a23*a32 - a12*a23*a34 - a13*a24*a32 - a14*a22*a33);
		
		ret.set(1, 0, a21*a34*a43 + a23*a31*a44 + a24*a33*a41 - a21*a33*a44 - a23*a34*a41 - a24*a31*a43);
		ret.set(1, 1, a11*a33*a44 + a13*a34*a41 + a14*a31*a43 - a11*a34*a43 - a13*a31*a44 - a14*a33*a41);
		ret.set(1, 2, a11*a24*a43 + a13*a21*a44 + a14*a23*a41 - a11*a23*a44 - a13*a24*a41 - a14*a21*a43);
		ret.set(1, 3, a11*a23*a34 + a13*a24*a41 + a14*a21*a33 - a11*a24*a33 - a13*a21*a34 - a14*a23*a31);
	
		ret.set(2, 0, a21*a32*a44 + a22*a34*a41 + a24*a31*a42 - a21*a34*a42 - a22*a31*a44 - a24*a32*a41);
		ret.set(2, 1, a11*a34*a42 + a12*a31*a44 + a14*a32*a41 - a11*a32*a44 - a12*a34*a41 - a14*a31*a42);
		ret.set(2, 2, a11*a22*a44 + a12*a24*a41 + a14*a21*a42 - a11*a24*a42 - a12*a21*a44 - a14*a22*a41);
		ret.set(2, 3, a11*a24*a32 + a12*a21*a34 + a14*a22*a31 - a11*a22*a34 - a12*a24*a31 - a14*a21*a32);
		
		ret.set(3, 0, a21*a33*a42 + a22*a31*a43 + a23*a32*a41 - a21*a32*a43 - a22*a33*a41 - a23*a31*a42);
		ret.set(3, 1, a11*a32*a43 + a12*a33*a41 + a13*a31*a42 - a11*a33*a42 - a12*a31*a43 - a13*a32*a41);
		ret.set(3, 2, a11*a23*a42 + a12*a21*a43 + a13*a22*a41 - a11*a22*a43 - a12*a23*a41 - a13*a21*a42);
		ret.set(3, 3, a11*a22*a33 + a12*a23*a31 + a13*a21*a32 - a11*a23*a32 - a12*a21*a33 - a13*a22*a31);
		return ret.times(1.f/det);
	}
	
	public void set(int idx, float f) {
		e_[idx] = f;
	}
	
	public void set(int row, int col, float f) {
		e_[row*row_size_+col] = f;
	}
	
	public float get(int idx) {
		return e_[idx];
	}
	
	public float get(int row, int col) {
		return e_[row*row_size_+col];
	}
	
	public float [] getMatrix() {
		return e_;
	}
	
	public float [] toArray() {
		return (float[])e_.clone();
	}

	public Matrix4f transpose() {
		Matrix4f m = new Matrix4f();
		for (int i=0; i<row_size_; i++)
			for (int j=0; j<row_size_; j++)
				m.set(j, i, get(i, j));
		return m;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "[";
		for (int i=0; i<len_; i++) {
			str += e_[i] + " ";
		}
		str += "]";
		return str;
	}
	
	

}
