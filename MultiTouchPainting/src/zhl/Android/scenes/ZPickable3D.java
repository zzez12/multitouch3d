package zhl.Android.scenes;

import zhl.Android.Multitouch.render.ZProjector;

public interface ZPickable3D {
	/*
	 * pick objects with the screen point (x, y)
	 */
	public boolean pick(ZProjector proj, float x, float y);
}
