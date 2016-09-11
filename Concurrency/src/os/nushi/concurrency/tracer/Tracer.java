package os.nushi.concurrency.tracer;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Tracer {
    private ArrayList <WrapperThread> threadList;

    Tracer() {
        threadList = new ArrayList < WrapperThread > ();
    }
 
    public void add(Thread t) {
        threadList.add(new WrapperThread(t));
    }
 
    public static void startDeadLockMonitor() {
        Thread monitoringThread = new Thread(() -> {
                System.out.println("---------Monitoring Deadlocks---------");
                while (Thread.activeCount() >= 2) deadLockMonitor();
        }, "DeadLock Monitor");
        monitoringThread.setDaemon(true);
		monitoringThread.start();
    }
 
    private static void deadLockMonitor() {
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] DevilThreads = mx.findDeadlockedThreads();
        if (DevilThreads != null && DevilThreads.length > 0) {
            System.out.println(currentTime() + " :: Deadlock detected ##########");
            for (int i = 0; i < DevilThreads.length; i++) {
                System.out.println("########## Thread id :" + DevilThreads[i]);
            }
            System.out.println("Exiting from system");
            System.exit(0);
        }
    }
 
    public void trace() {
        new Thread(() -> {
                while (Thread.activeCount() >= 2) {
                    if (isStateChanged()) System.out.println (currentTime() + " :: "+threadList);
                }
        }, "Tracer").start();
    }
 
    private boolean isStateChanged(){
    	for (WrapperThread wT : threadList) {
    		//if(!wT.originalThread.isAlive()) //remove wT
    		if(wT.isStateChanged()) return true;
		}
    	return false;
    }
 
    public static String currentTime() {
        return (new Timestamp(System.currentTimeMillis())).toString();
    }
}