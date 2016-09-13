/*
 * (C) Copyright 2016 NaturalIntelligence.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Written by: Amit Gupta
 * https://github.com/NaturalIntelligence/
 * 
 */
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
	
	public boolean isStuck(){
		State state = originalThread.getState();
		if(state == Thread.State.BLOCKED || state == Thread.State.TIMED_WAITING || state == Thread.State.WAITING){
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
