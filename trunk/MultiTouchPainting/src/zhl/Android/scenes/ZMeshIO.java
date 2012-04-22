package zhl.Android.scenes;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ZMeshIO {
	boolean load(String fileName) throws FileNotFoundException, IOException;
	boolean save(String fileName);
}
