package zhl.Android.Multitouch.paint;

import java.io.FileNotFoundException;
import java.io.IOException;

import zhl.Android.Multitouch.render.ZView;
import zhl.Android.scenes.ZDataManager;
import zhl.Android.scenes.ZMesh;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MultiTouchPaintingActivity extends Activity {
	private static final String LOG_TAG = MultiTouchPaintingActivity.class.getSimpleName();
	private ZView zView_;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initUIs();
 //       zView_ = new ZView(this);
 //       setContentView(zView_);
    }
    
    void initUIs() {
//    	findViewById(R.id.bnReset).setOnClickListener(clickListener);
//    	findViewById(R.id.imaBnCone).setOnClickListener(clickListener);
//    	findViewById(R.id.imaBnCube).setOnClickListener(clickListener);
//    	findViewById(R.id.imaBnCylinder).setOnClickListener(clickListener);
//    	findViewById(R.id.imaBnSphere).setOnClickListener(clickListener);
//    	findViewById(R.id.imaBnDelete).setOnClickListener(clickListener);
    }
    
    public void setToolTipText(String text) {
    	TextView tvToolTip = (TextView)findViewById(R.id.tvToolTip);
    	tvToolTip.setText(text);
    }
    
    public void clickHandler(View v) {
    	switch (v.getId()) {
		case R.id.bnReset:
			ZDataManager.getDataManager().resetAll();
			break;
		case R.id.imaBnCone:
			try {
				ZDataManager.getDataManager().addMesh(ZMesh.strNameCone);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imaBnCube:
			try {
				ZDataManager.getDataManager().addMesh(ZMesh.strNameCube);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imaBnCylinder:
			try {
				ZDataManager.getDataManager().addMesh(ZMesh.strNameCylinder);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imaBnSphere:
			try {
				ZDataManager.getDataManager().addMesh(ZMesh.strNameSphere);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imaBnDelete:
			//ZDataManager.getDataManager().removeMeshes();
			break;
		}
    }
    
}