package zhl.Android.Multitouch.touch;

import java.util.Stack;
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
	private Stack<Long> tagTimes_ = new Stack<Long>();
	
	public ZTimer() {
		start();
	}
	
	public void start() {
		createTime_ = System.currentTimeMillis();
		stopTime_ = createTime_;
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
	
	public void tagTime() {
		tagTimes_.push(System.currentTimeMillis());
	}
	
	public long getDurationFromTagTime() {
		long curT = System.currentTimeMillis();
		long tagT = tagTimes_.pop();
		return curT - tagT;
	}
}
