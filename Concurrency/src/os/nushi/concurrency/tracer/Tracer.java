package os.nushi.concurrency.tracer;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;

public class Tracer {
    private static List<WrapperThread> threadList = new CopyOnWriteArrayList<WrapperThread>();
    private static List<Lock> locksList = new CopyOnWriteArrayList<Lock>();

    public static void add(Thread t) {
        threadList.add(new WrapperThread(t));
    }
    
    public static void add(Lock l) {
    	locksList.add(l);
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
        mx.setThreadContentionMonitoringEnabled(true);
        checkDeadLock(mx.findDeadlockedThreads());
        checkDeadLock(mx.findMonitorDeadlockedThreads());
        
    }

	private static void checkDeadLock(long[] lockedThreads) {
		if (lockedThreads != null && lockedThreads.length > 0) {
            System.out.println(currentTime() + " :: Deadlock detected ##########");
            for (int i = 0; i < lockedThreads.length; i++) {
                System.out.println("########## Thread id :" + lockedThreads[i]);
            }
            System.out.println("Exiting from system");
            System.exit(0);
        }
	}
 
    public static void trace() {
        Thread tracerThread = new Thread(() -> {
                while (Thread.activeCount() >= 2) {
                    if (isStateChanged()) System.out.println (currentTime() + " :: "+threadList);
                }
        }, "Tracer");
        tracerThread.setDaemon(true);
        tracerThread.start();
    }
 
    private static boolean isStateChanged(){
    	boolean allStuck = false;
    	for (WrapperThread wT : threadList) {
    		//if(!wT.originalThread.isAlive()) //remove wT
    		if(wT.isStuck()) 
    			allStuck &= true;
    		else 
    			allStuck &= false;
    		if(wT.isStateChanged()) return true;
		}
    	if(allStuck){
    		for (WrapperThread wT : threadList) {
    			dumpLocks(wT.originalThread);
    		}
    	}
    	return false;
    }
    
    private static void dumpLocks(Thread t) {
    	ThreadInfo threadInfo = ManagementFactory.getThreadMXBean().getThreadInfo(t.getId());
    	System.out.println(t.getName() + " has lock : " + threadInfo.getLockName());
	}

	public static String currentTime() {
        return (new Timestamp(System.currentTimeMillis())).toString();
    }
}