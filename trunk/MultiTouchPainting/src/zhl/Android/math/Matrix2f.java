package zhl.Android.math;

public class Matrix2f {
	public static final int row_size = 2;
	public static final int col_size = 2;
	public float []e_ = new float[4];	// row-major matrix
	
	public Matrix2f() {
		e_[0] = e_[1] = e_[2] = e_[3] = 0.f;
	}
	
	public Matrix2f(Matrix2f m) {
		for (int i=0; i<4; i++)
			e_[i] = m.e_[i];
	}
	
	public Matrix2f(float []arr) {
		for (int i=0; i<4; i++)
			e_[i] = arr[i];
	}
	
	public Matrix2f(float [][]arr) {
		for (int i=0; i<row_size; i++)
			for (int j=0; j<col_size; j++)
				e_[i*col_size+j] = arr[i][j];
	}
	
	// using column vector
	public Matrix2f(Vector2f v0, Vector2f v1) {
		e_[0] = v0.x_;
		e_[1] = v1.x_;
		e_[2] = v0.y_;
		e_[3] = v1.y_;
	}
	
	public Matrix2f(float a, float b, float c, float d) {
		e_[0] = a;
		e_[1] = b;
		e_[2] = c;
		e_[3] = d;
	}
	
	public static Matrix2f identityMatrix() {
		return new Matrix2f(1,0,0,1);
	}
	
	public float determinate() {
		return e_[0]*e_[3] - e_[1]*e_[2];
	}
	
	public Matrix2f plus(Matrix2f m) {
		return new Matrix2f(this.e_[0]+m.e_[0],
							this.e_[1]+m.e_[1],
							this.e_[2]+m.e_[2],
							this.e_[3]+m.e_[3]);
	}
	
	public Matrix2f minus(Matrix2f m) {
		return new Matrix2f(this.e_[0]-m.e_[0],
				this.e_[1]-m.e_[1],
				this.e_[2]-m.e_[2],
				this.e_[3]-m.e_[3]);
	}
	
	public Matrix2f multi(Matrix2f m) {
		return new Matrix2f(this.e_[0]*m.e_[0]+this.e_[1]*m.e_[2],
							this.e_[0]*m.e_[1]+this.e_[1]*m.e_[3],
							this.e_[2]*m.e_[0]+this.e_[3]*m.e_[2],
							this.e_[2]*m.e_[1]+this.e_[3]*m.e_[3]);
	}
	
	public Matrix2f times(float f) {
		return new Matrix2f(this.e_[0]*f,
				this.e_[1]*f,
				this.e_[2]*f,
				this.e_[3]*f);
	}
	
	public Vector2f multi(Vector2f v) {
		return new Vector2f(e_[0]*v.x_+e_[1]*v.y_, e_[2]*v.x_+e_[3]*v.y_);
	}
	
	public Matrix2f transpose() {
		return new Matrix2f(e_[0], e_[2], e_[1], e_[3]);
	}
	
	public Matrix2f inverse() {
		float det = e_[0]*e_[3] - e_[1]*e_[2];
		if (Float.isNaN(det)) {
			throw new ArithmeticException();
		}
		return new Matrix2f(e_[3]/det, -e_[1]/det, -e_[2]/det, e_[0]/det);
	}
	
	public Matrix2f inverseTranspose() {
		float det = e_[0]*e_[3] - e_[1]*e_[2];
		if (Float.isNaN(det)) throw new ArithmeticException();
		return new Matrix2f(e_[3]/det, -e_[2]/det, -e_[1]/det, e_[0]/det);
	}
	
	public float sqNorm() {
		return e_[0]*e_[0] + e_[1]*e_[1] + e_[2]*e_[2] + e_[3]*e_[3];
	}
	
	public Matrix2f orthogonalFactor(float eps) {
		Matrix2f Q = new Matrix2f(this);
		Matrix2f Q2 = new Matrix2f();
		float err = 0.f;
		int count = 0;
		do {
			Q2 = (Q.plus(Q.inverseTranspose())).times(0.5f);
			err = (Q2.minus(Q)).sqNorm();
			Q = new Matrix2f(Q2);
			count ++;
		} while(err>eps);
		return Q2;
	}
}
