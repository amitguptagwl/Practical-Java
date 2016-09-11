package os.nushi.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class PerformanceTester {

	long startTime = 0;
	
	@Test
	public void test() {
		loop();
		startTime = System.nanoTime();
		executorRunnableTest();
		System.out.println("After executorRunnable :" + (System.nanoTime() - startTime)/1000000.00);
		
		
		loop();
		startTime = System.nanoTime();
		executorCallableTest();
		System.out.println("After executorCallable :" + (System.nanoTime() - startTime)/1000000.00);
		
		loop();
		startTime = System.nanoTime();
		threadRunnable();
		System.out.println("After threadRunnable :" + (System.nanoTime() - startTime)/1000000.00);
		
	}

	private void loop() {
		for(int i=0;i<100000;i++);
	}

	private void executorRunnableTest(){
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		executorService.execute(new Runnable() {
		    public void run() {
		        loop();
		        System.out.println("From Executor Service");
		    }
		});

		executorService.shutdown();
	}
	
	private void executorCallableTest(){
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		executorService.submit(new Callable<String>() {
		 	@Override
			public String call() throws Exception {
				loop();
		        System.out.println("From Executor Service");
		        return "Amit";
			}
		});

		executorService.shutdown();
	}
	
	private void threadRunnable(){
		Thread T1 = new Thread(new Runnable() {
		    public void run() {
		        loop();
		        System.out.println("From Executor Service");
		    }
		},"Runnable thread");
		T1.start();
	}
	
	
}
