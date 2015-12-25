package android.jason.mobileapps;

import android.os.Process;

public abstract class BaseThread extends Thread {

	private volatile boolean mQuit = false;

	public BaseThread() {

	}

	/**
	 * prepare if needed
	 */
	public boolean prepare() {

		return true;
	}

	/**
	 * run body
	 */
	public abstract void execute() throws Exception; // third modify,second modify maybe not neccessary

	/**
	 * recycle if needed
	 */
	public void recycle() {

	}

	/**
	 * stop thread
	 */
	public final void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		// TODO
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		if (!prepare()) {
			return;
		}
		while (true) {
			if (mQuit) { // second modify
				recycle();
				return;
			}
			try {
				execute();
			} catch (Exception e) {// } catch (InterruptedException e) {
				if (mQuit) {
					recycle();
					return;
				}
				continue;
			}
		}
	}
}