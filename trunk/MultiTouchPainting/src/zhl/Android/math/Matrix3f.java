package zhl.Android.math;

public class Matrix3f {
	public static final int len_ = 9;
	public static final int row_size_ = 3;
	public static final int col_size_ = 3;
	private float [] e_ = new float[len_];
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//return super.toString();
		return "+-" + e_[0] + "--" + e_[1] + "--" + e_[2] + "--\n"
			+  "--" + e_[3] + "--" + e_[4] + "--" + e_[5] + "--\n"
			+  "--" + e_[6] + "--" + e_[7] + "--" + e_[8] + "-+";
	}

	public Matrix3f() {}
	
	public Matrix3f(float[] arr) {
		for (int i=0; i<len_; i++) e_[i] = arr[i];
	}
	public Matrix3f(float[][] arr) {
		for (int x=0; x<row_size_; x++)
			for (int y=0; y<col_size_; y++)
				set(x, y, arr[x][y]);
	}
	public Matrix3f(Vector3f v1, Vector3f v2, Vector3f v3) {
		for (int i=0; i<row_size_; i++) {
			set(i, 0, v1.get(i));
			set(i, 1, v2.get(i));
			set(i, 2, v3.get(i));
		}
	}
	
	public Matrix3f(Matrix3f m) {
		
	}
	
	public void set(int idx, float f) {
		e_[idx] = f;
	}
	public float get(int idx) {
		return e_[idx];
	}
	public void set(int row, int col, float f) {
		e_[row*row_size_+col] = f;
	}
	public float get(int row, int col) {
		return e_[row*row_size_+col];
	}
	
	public float [] toArray() {
		return (float[])e_.clone();
	}
	
	public void clear() {
		for (int i=0; i<len_; i++) e_[i] = 0.0f;
	}
	
	public float trace() {
		return e_[0]+e_[4]+e_[8];
	}
	
	public float sqNorm() {
		float sq = 0.f;
		for (int i=0; i<len_; i++) sq += e_[i]*e_[i];
		return sq;
	}
	public float determinant() {
		return 	e_[0] * e_[4] * e_[8] +
				e_[1] * e_[5] * e_[6] +
				e_[2] * e_[3] * e_[8] -
				e_[0] * e_[5] * e_[7] -
				e_[1] * e_[3] * e_[8] -
				e_[2] * e_[4] * e_[6];
	}
	public Matrix3f transpose() {
		Matrix3f m = new Matrix3f();
		for (int x = 0; x < row_size_; x++)
			for (int y = 0; y < col_size_; y++)
				m.set(y, x, get(x, y));
		return m;
	}
	
	public Matrix3f inverse() {
		float a = e_[0];
		float b = e_[1];
		float c = e_[2];
		float d = e_[3];
		float E = e_[4];
		float f = e_[5];
		float g = e_[6];
		float h = e_[7];
		float i = e_[8];
		float det = a * (E * i - f * h) - b * (d * i - f * g) + c * (d * h - E * g);
		if (det == 0) throw new ArithmeticException();

		Matrix3f inv = new Matrix3f();
		inv.set(0, (E * i - f * h) / det);
		inv.set(1, (c * h - b * i) / det);
		inv.set(2, (b * f - c * E) / det);
		inv.set(3, (f * g - d * i) / det);
		inv.set(4, (a * i - c * g) / det);
		inv.set(5, (c * d - a * f) / det);
		inv.set(6, (d * h - E * g) / det);
		inv.set(7, (b * g - a * h) / det);
		inv.set(8, (a * E - b * d) / det);
		return inv;
	}
	
	public Matrix3f inverseSVD() {
		// TODO: undo.
		return inverse();
	}
	
	public Matrix3f inverseTranspose() {
		float a = e_[0];
		float b = e_[1];
		float c = e_[2];
		float d = e_[3];
		float E = e_[4];
		float f = e_[5];
		float g = e_[6];
		float h = e_[7];
		float i = e_[8];
		float det = a * (E * i - f * h) - b * (d * i - f * g) + c * (d * h - E * g);
		if (det == 0) throw new ArithmeticException();

		Matrix3f inv = new Matrix3f();
		inv.set(0, (E * i - f * h) / det);
		inv.set(3, (c * h - b * i) / det);
		inv.set(6, (b * f - c * E) / det);
		inv.set(1, (f * g - d * i) / det);
		inv.set(4, (a * i - c * g) / det);
		inv.set(7, (c * d - a * f) / det);
		inv.set(2, (d * h - E * g) / det);
		inv.set(5, (b * g - a * h) / det);
		inv.set(8, (a * E - b * d) / det);
		return inv;
	}
	
	public Matrix3f orthogonalFactor(float eps) {
		// TODO: undo.
		Matrix3f Q = new Matrix3f(this);
		Matrix3f Q2 = new Matrix3f();
		double err = 0;
		do
		{
//			Q2 = (Q + Q.inverseTranspose()) / 2.0;
//			err = (Q2 - Q).SqNorm();
//			Q = Q2;
		} while (err > eps);

		return Q2;
	}
	
	public Matrix3f orthogonalFactorSVD() {
		// TODO: undo
		return null;
	}
	
	public Matrix3f orthogonalFactorIter() {
		// TODO: undo
		return null;
	}
	
	public static Matrix3f identityMatrix() {
		Matrix3f m = new Matrix3f();
		m.set(0, 1.f);
		m.set(4, 1.f);
		m.set(8, 1.f);
		return m;
	}
	
	public Vector3f multiply(Vector3f v) {
		Vector3f ret = new Vector3f();
		ret.x_ = e_[0]*v.x_ + e_[1]*v.y_ + e_[2]*v.z_;
		ret.y_ = e_[3]*v.x_ + e_[4]*v.y_ + e_[5]*v.z_;
		ret.z_ = e_[6]*v.x_ + e_[7]*v.y_ + e_[8]*v.z_;
		return ret;
	}
	
	public Matrix3f multiply(Matrix3f m) {
		Matrix3f ret = new Matrix3f();
		for (int x=0; x<row_size_; x++)
			for (int y=0; y<col_size_; y++) {
				float f = 0.f;
				for (int z=0; z<row_size_; z++) 
					f += this.get(x, z)*m.get(z, y);
				ret.set(x, y, f);
			}
		return ret;
	}
	
	public Matrix3f plus(Matrix3f m) {
		Matrix3f ret = new Matrix3f();
		for (int i=0; i<len_; i++)
			ret.set(i, this.get(i)+m.get(i));
		return ret;
	}
	
	public Matrix3f minus(Matrix3f m) {
		Matrix3f ret = new Matrix3f();
		for (int i=0; i<len_; i++)
			ret.set(i, this.get(i)-m.get(i));
		return ret;
	}
	
	public Matrix3f times(float f) {
		Matrix3f ret = new Matrix3f();
		for (int i=0; i<len_; i++)
			ret.set(i, this.get(i)*f);
		return ret;
	}
	
	public Matrix3f divide(float f) {
		Matrix3f ret = new Matrix3f();
		for (int i=0; i<len_; i++)
			ret.set(i, this.get(i)/f);
		return ret;
	}
	
}
