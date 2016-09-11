package os.nushi.concurrency.tracer;
public class Main {
	String lock="lock value";

	/*public synchronized void acquire() {
		System.out.println("acquire");
		try {
			Thread.sleep(5000);
			synchronized(lock){
				System.out.println("acquire::lock");
		        wait(1);
		    }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 
	public synchronized void modify(){
		System.out.println("modify");
	    try {
			Thread.sleep(5000);
			synchronized(lock){
				System.out.println("modify::lock");
		        wait(1);
		    }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
	Integer a = 10;
	
	public void acquire(){
	    synchronized(a){
	        print("acquire()");
	        try{
	            a.wait(5000);
	            print("I have awoken");
	            print("" + a);
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	    print("Leaving acquire()");
	}
	 
	public void modify(int n){
	    print("Entered in modify");
	    synchronized(a){
	        try{
	            a.wait(2000);
	            this.a=n;
	            print("new value" + a);
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	}
	
	public void print(String msg){
		System.out.println(Thread.currentThread().getName() +" :: " + msg);
	}
	
	public static void main(String[] args) {
		Main mainObj = new Main();
		Runnable A = () -> mainObj.acquire();
		Runnable B = () -> mainObj.modify(30);
		
		Thread tA= new Thread(A,"A");
		Thread tB= new Thread(B,"B");
		
		Tracer t = new Tracer();
		t.add(tA);
		t.add(tB);
		t.trace();
		
		tA.start();
		tB.start();
	}
}
