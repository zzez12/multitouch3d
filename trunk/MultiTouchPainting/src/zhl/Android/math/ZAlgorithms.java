package zhl.Android.math;


public class ZAlgorithms {
	
	public static final float eps_float = 0.0001f;
	
	
	public static boolean inInTriangle(Vector2f vrt2D, Vector2f v0, Vector2f v1, Vector2f v2, Vector2f intersect, float [] depth) {
		Vector2f v02 = v0.minus(v2);
		Vector2f v12 = v1.minus(v2);
		Matrix2f T = new Matrix2f(v02, v12);
		float det = T.determinate();
		if (det==0.f) return false;
		
		Vector2f coord = T.inverse().multi(vrt2D.minus(v2));
		float c0 = coord.x_;
		float c1 = coord.y_;
		float c2 = 1.f - c0 - c1;
		if (c0<0 || c2<0 || c2<0) return false;
		
		return false;
	}
	
	public static boolean intersectBallByRay(Vector3f rayStart, Vector3f rayEnd, Vector3f ballCenter, float ballRadius) {
		//boolean bRet = false;
		float rsc = rayStart.minus(ballCenter).length();
		float rec = rayEnd.minus(ballCenter).length();
		if ((rsc-ballRadius)*(rec-ballRadius)<0) return true;	// one inside, and the other outside
		else if (rsc<ballRadius && rec<ballRadius) return false; // two are both inside
		else {	// two are both outside
			Vector3f dir = rayStart.minus(rayEnd).normalize();
			float t = rayStart.minus(ballCenter).dot(dir);
			Vector3f projV = rayStart.minus(dir.times(t));
			float rpc = projV.minus(ballCenter).length();
			if (rpc<ballRadius) return true;
			else return false;
		}
		//return bRet;
	}
	
	public static final class TriangleIntersection {
		public int intersection;
		public Vector3f intersectionP = null;
		public TriangleIntersection() {
			intersection = -2;
		}
	}
	
	/**
	 * calculate whether the ray intersecting with the triangle
	 * @param startP	end point of the ray
	 * @param endP		the other end point of the ray
	 * @param triVrt0	the vertices of the triangle
	 * @param triVrt1   the vertices of the triangle
	 * @param triVrt2   the vertices of the triangle
	 * @param intersectI	the intersection point (if exists)
	 * @return	-1: no intersection
	 * 			 0: intersect with the plane but outside the triangle
	 * 			 1: intersect in the triangles
	 * NOTE: untested!!
	 */
	public static TriangleIntersection intersect_RayTriangle(Vector3f startP, Vector3f endP, Vector3f triVrt0, Vector3f triVrt1, Vector3f triVrt2) {
		Vector3f u, v, n;	// triangle vectors
		Vector3f dir, w0, w;// ray vectors
		float r, a, b;		// params to calc ray-plane intersect
		TriangleIntersection ret = new TriangleIntersection();
		
		// get triangle edge vectors and plane normal
		u = triVrt1.minus(triVrt0);
		v = triVrt2.minus(triVrt0);
		n = u.cross(v).normalize();
		
		if (n.equals(new Vector3f(0.f, 0.f, 0.f))) {
			ret.intersection = -1;
			return ret;
		}
		
		dir = startP.minus(endP);
		w0 = startP.minus(triVrt0);
		
		a = n.dot(w0)*(-1.f);
		b = n.dot(dir);
		
		if (Math.abs(b)<eps_float) {	// ray is parallel to triangle plane
			if (a==0)		{	// ray lies in triangle plane
				ret.intersection = 2;
				return ret;
			}
			else {
				ret.intersection = 0;
				return ret;		// ray disjoint from plane
			}
		}
		
		// get intersect point of ray with triangle plane
		r = a/b;
		if (r>0.0)	{// ray goes away from triangle
			ret.intersection = 0;
			return ret;
		}
		// for segment, also test if (r>1.0) ==> no intersect
		
		ret.intersectionP = startP.plus(dir.times(r));
		
		// is intersectI inside T?
		float uu, uv, vv, wu, wv, D;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w = ret.intersectionP.minus(triVrt0);
		wu = w.dot(u);
		wv = w.dot(v);
		D = uv*uv - uu*vv;
		
		// get and test parametric coords
		float s, t;
		s = (uv*wv-vv*wu)/D;
		if (s<0.f || s>1.0)	{// I is outside T
			ret.intersection = 0;
			return ret;
		}
		t = (uv*wu-uu*wv)/D;
		if (t<0.f || t>1.f)	{// I is outside T
			ret.intersection = 0;
			return ret;
		}
		if (s+t<0.f || s+t>1.f) {
			ret.intersection = 0;
			return ret;
		}
		
		ret.intersection = 1;
		return ret;
	}
}
