package zhl.Android.scenes;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import zhl.Android.math.Vector3f;
import zhl.Android.math.Vector3s;

public class ZParseOBJ {
	public static class ZBuffers {
		public float [] verticesPos = null;
		public short [] faceIndices = null;
		public ZBuffers(){}
		
		public void setData(Vector<Vector3f> pos, Vector<Vector3s> fIdx) {
			verticesPos = new float [pos.size()*3];
			for (int i=0; i<pos.size(); i++) {
				verticesPos[i*3] = pos.get(i).x_;
				verticesPos[i*3+1] = pos.get(i).y_;
				verticesPos[i*3+2] = pos.get(i).z_;
			}
			faceIndices = new short [fIdx.size()*3];
			for (int i=0; i<fIdx.size(); i++) {
				faceIndices[i*3+0] = fIdx.get(i).e_[0];
				faceIndices[i*3+1] = fIdx.get(i).e_[1];
				faceIndices[i*3+2] = fIdx.get(i).e_[2];
			}
		}
	}
	
	public static ZBuffers parse(FileReader fr) throws IOException {
		ZBuffers buffer = new ZBuffers();
		BufferedReader br = new BufferedReader(fr);
		Vector<Vector3f> pos = new Vector<Vector3f>();
		Vector<Vector3s> indices = new Vector<Vector3s>();
		String line;
		while ((line = br.readLine())!=null) {
			if (line=="") continue;
			String [] chars = line.split(" ");
			if (chars[0].equalsIgnoreCase("#")) {
				// comments
				continue;
			}
			else if (chars[0].equalsIgnoreCase("v")) {
				// vertices
				float v1, v2, v3;
				v1 = Float.parseFloat(chars[1]);
				v2 = Float.parseFloat(chars[2]);
				v3 = Float.parseFloat(chars[3]);
				pos.add(new Vector3f(v1, v2, v3));
			} 
			else if (chars[0].equalsIgnoreCase("f")) {
				// faces
				short v1, v2, v3;
				v1 = parseFaceString(chars[1]);
				v2 = parseFaceString(chars[2]);
				v3 = parseFaceString(chars[3]);
				indices.add(new Vector3s(v1, v2, v3));
			}
		}
		buffer.setData(pos, indices);
		return buffer;
	}
	
	public static short parseFaceString(String str) {
		// some possible input:
		// 1. vrtIdx vrtIdx ...
		// 2. vrtIdx/texIdx vrtIdx/texIdx
		String [] chars = str.split("/");
		Short s = Short.parseShort(chars[0]);
		return (short)(s-1);
	}

}
