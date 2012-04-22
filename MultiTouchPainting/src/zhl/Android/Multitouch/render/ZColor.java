package zhl.Android.Multitouch.render;

public class ZColor {
	public static final float [] colorRed = {1.0f, 0.f, 0.f, 1.f};
	public static final float [] colorGreen = {0.f, 1.f, 0.f, 1.f};
	public static final float [] colorBlue = {0.f, 0.f, 1.f, 1.f};
	public static final float [] colorWhite = {1.f, 1.f, 1.f, 1.f};
	public static final float [] colorBlack = {0.f, 0.f, 0.f, 1.f};
	public static final float [][] colorTable = {colorBlack, colorWhite, colorRed, colorGreen, colorBlue,
		{0.3f, 0.3f, 0.f, 1.f}, {0.f, 0.3f, 0.3f, 1.f} };
	
	public static final float [] axisColorX = {1.f, 0.f, 0.f, 1.f};
	public static final float [] axisColorY = {0.f, 1.f, 0.f, 1.f};
	public static final float [] axisColorZ = {0.f, 0.f, 1.f, 1.f};
	public static final float [] axisColorX_fadeout = {1.f, 0.f, 0.f, 0.f};
	public static final float [] axisColorY_fadeout = {0.f, 1.f, 0.f, 0.f};
	public static final float [] axisColorZ_fadeout = {0.f, 0.f, 1.f, 0.f};
	
	public static float[] getColorFromColorTable(int index) {
		return colorTable[index % colorTable.length];
	}
	
	public static void setColor(float[] dest, float[] col) {
		for (int i=0; i<4; i++) {
			dest[i] = col[i];
		}
	}
	
	public static float[] fromRGBA(float r, float g, float b, float a) {
		return new float[]{r, g, b, a};
	}
}
