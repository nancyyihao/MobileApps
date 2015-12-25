package android.jason.mobileapps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ToastUtil extends BaseThread {
	private static final int SHOW_TIME = 2000; // 显示时间
	private static final int QUEUE_SIZE = 120; // 队列大小
	private static final int QUEUE_SIZE_LIMIT = 100; // 限制队列大小
	private static final int FLAG_SHOW = 1000; // 显示
	private static final int FLAG_HIDE = 1001; // 隐藏
	private static final int FLAG_CLEAR = 1002; // 清理消息队列
	private static final String QUITMSG = "@bian_#feng_$market_%toast_&quit_*flag"; // 退出的标记

	private static BlockingQueue<String> mMsgQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE); // 消息缓存队列

	private static ToastUtil mToast;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mParams;
	private View toastView;
	private TextView tvAlert;

	@SuppressLint("InflateParams")
	private ToastUtil(Context context) {
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST; //TYPE_SYSTEM_OVERLAY
		mParams.windowAnimations = android.R.style.Animation_Toast;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
		mParams.alpha = 1f;// 透明度，0全透 ，1不透
		mParams.verticalMargin = 0.75f;
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

		toastView = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
		tvAlert = (TextView) toastView.findViewById(R.id.tvAlert);

		start();
	}

	/**
	 * 初始化消息显示
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (null == mToast) {
			mToast = new ToastUtil(context);
		}
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case FLAG_SHOW:
				String str = msg.obj.toString();
				if (!TextUtils.isEmpty(str)) {
					showMsg(str);
				}
				break;
			case FLAG_HIDE:
				hideMsg();
				break;
			case FLAG_CLEAR:
				showMsg("操作异常，消息太多");
				break;

			default:
				break;
			}
		};
	};

	private void showMsg(String msg) {
		try {
			tvAlert.setText(msg);
			if (null == toastView.getParent()) {
				mWindowManager.addView(toastView, mParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void hideMsg() {
		try {
			if (null != toastView.getParent()) {
				mWindowManager.removeView(toastView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示消息
	 * 
	 * @param msg
	 *            显示的内容
	 */
	public static void show(String msg) {
		try {
			mMsgQueue.put(msg); // block
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void show(Context context, int id) {
		try {
			mMsgQueue.put(context.getResources().getString(id)); // block
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出
	 */
	public static void eixt() {
		try {
			mMsgQueue.put(QUITMSG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		try {
			String msgStr = mMsgQueue.take();
			
			if (QUITMSG.equals(msgStr)) {
				exitToast();
				return;
			}

			Message msg = mHandler.obtainMessage();
			if (null == msg) {
				msg = new Message();
			}
			msg.what = FLAG_SHOW;
			msg.obj = msgStr;
			mHandler.sendMessage(msg);

			Thread.sleep(SHOW_TIME);

			if (mMsgQueue.size() == 0) {
				mHandler.sendEmptyMessage(FLAG_HIDE);
			}

			if (mMsgQueue.size() > QUEUE_SIZE_LIMIT) {
				mMsgQueue.clear();

				mHandler.sendEmptyMessage(FLAG_CLEAR);
				Thread.sleep(SHOW_TIME);
				mHandler.sendEmptyMessage(FLAG_HIDE);
			}

			System.out.println(">>>>>" + mMsgQueue.size());
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(FLAG_HIDE);
		}
	}

	/**
	 * 退出，清理内存
	 */
	private void exitToast() {
		try {
			hideMsg();

			quit();
			mMsgQueue.clear();
			mMsgQueue = null;
			mToast = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}