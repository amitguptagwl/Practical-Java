package os.nushi.concurrency;

public class DaemonThreads {
	private static class MyDaemonThread extends Thread {

		public MyDaemonThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println(getState());
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new MyDaemonThread();
		thread.start();
		//thread.join();
		
		for (int i = 0; i < 10 ; i++) {
			Thread.sleep(1000);
			System.out.println(thread.isAlive());
		}
		
		//System.out.println(thread.isAlive());
	}
}
