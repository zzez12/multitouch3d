package zhl.Android.Multitouch.touch;

import java.util.Timer;

import android.os.Handler;
import android.os.Message;

public class ZTimer {
//	Timer timer_ = new Timer();
//	Handler handler_ = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//		}
//		
//	};
	
	public long createTime_;
	public long stopTime_;
	public long currentTick_;
	
	
	public ZTimer() {
		start();
	}
	
	public void start() {
		createTime_ = System.currentTimeMillis();
	}
	
	public void stop() {
		stopTime_ = System.currentTimeMillis();
	}
	
	public long getCurrentTick() {
		currentTick_ = System.currentTimeMillis();
		return currentTick_;
	}
	
	public long getDuration() {
		return stopTime_ - createTime_;
	}
}
