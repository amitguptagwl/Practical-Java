package os.nushi.concurrency.tracer;
import java.lang.Thread.State;

public class WrapperThread{
	private String name;
	protected Thread originalThread;
	private State lastState;

	public WrapperThread(Thread t) {
		originalThread = t;
		name = t.getName();
		lastState = originalThread.getState();
		System.out.println(Tracer.currentTime() + " :: " + this);
	}
	
	public boolean isStateChanged() {
		State state = originalThread.getState();
		if(lastState != state){
			lastState = state;
			return true;
		}
		return false;
	}

	public State currentState() {
		return originalThread.getState();
	}
	
	@Override
	public String toString() {
		return name+":"+currentState();//+":"+printArray(originalThread.getStackTrace());
	}
	
	public String printArray(StackTraceElement[] arr){
		StringBuilder stackTrace = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			stackTrace.append(arr[i].toString());
		}
		return stackTrace.toString();
	}

}
