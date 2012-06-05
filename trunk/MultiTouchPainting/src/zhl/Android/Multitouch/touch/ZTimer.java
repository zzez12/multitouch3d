package zhl.Android.Multitouch.touch;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ZTimer {
	public static final int ZTimerWhatTag = 1;
	
	private int timerInterval_ = 30;
	Timer timer_ = new Timer();
	private ZTimerTick timerTickListener_;
	
	public interface ZTimerTick {
		void onTimerTick();
	}	
	
	public static class ZEmptyTimerTick implements ZTimerTick {
		public static final String LOG_TAG = ZEmptyTimerTick.class.getSimpleName();
		public void onTimerTick() {
			Log.d(LOG_TAG, "ZEmptyTimerTick.onTimerTick()");
		}
	}
	
	Handler handler_ = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what==ZTimerWhatTag) {
				timerTickListener_.onTimerTick();
			}
			super.handleMessage(msg);
		}	
	};
	
	TimerTask task_ = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			message.what = ZTimerWhatTag;
			handler_.sendMessage(message);
		}
		
	};
	

	
	public long createTime_;
	public long stopTime_;
	public long currentTick_;
	private Stack<Long> tagTimes_ = new Stack<Long>();
	
	public ZTimer() {
		start();
		init(null);
		timer_.schedule(task_, timerInterval_);
	}
	
	public ZTimer(ZTimerTick timerListener) {
		start();
		init(timerListener);
		timer_.schedule(task_, 0, timerInterval_);
	}
	
	private void init(ZTimerTick timerListener) {
		if (timerListener==null) {
			timerTickListener_ = new ZEmptyTimerTick();
		}
		else {
			timerTickListener_ = timerListener;
		}
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
