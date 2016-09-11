package os.nushi.concurrency.cookiemaking2;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
 
/*
* CookieMaker bakes N cookies at a time. He can bake 1 type of cookie only
* Required containers must be installed in prior to bake a cookie.
*
* @author Amit Gupta
*
*/
 
public class CookieMaker {
    EnumMap < Ingredient, IngredientContainer > containers;
    private Semaphore bakingCapacity;
    Cookie cookie;
    private Lock bakingLock;
 
    public CookieMaker(){
        containers = new EnumMap < Ingredient, IngredientContainer > (Ingredient.class);
        System.out.println("I can bake upto " + bakingCapacity + " cookies in parallel.");
    }
 
    public void setCookie(Cookie cookie) {
    	//TODO: You can not change cookie type if baking is in progress.
        this.cookie = cookie;
    }
   
    public void addContainer(IngredientContainer container) {
        containers.put(container.TYPE, container);
        System.out.println(container.TYPE.toString() + " container added");
    }
    
    public void removeContainer(IngredientContainer container) {
    	//TODO: You can not remove a container if baking is in progress.
        containers.remove(container.TYPE, container);
        System.out.println(container.TYPE.toString() + " container removed");
    }
    
    public void enableParallelBaking(boolean flag) {
    	if(!flag)
    		bakingLock = new ReentrantLock();
    	else
    		bakingLock = null;
	}
 
	public void maxParallelBakingCapacity(int bakingCapacity) throws Exception {
		this.bakingCapacity = new Semaphore(bakingCapacity);
		if(bakingCapacity < 2){
        	throw new Exception("Parallel baking capacity is too low.");
        }
	}
	
    int counter = 0;
    public void startBaking() throws InterruptedException{
    	if(bakingCapacity.getQueueLength() > 0)
    		System.out.println("I am waiting to bake " + bakingCapacity.getQueueLength() + " cookies.");
        
    	bakingCapacity.acquire();
	        System.out.println("I am free to bake " + bakingCapacity.availablePermits() + " more cookies.");
	        
	        new Thread(() -> start(), "Maker_"+ counter++).start();
        bakingCapacity.release();
    }
    
    private void start() {
    	if(null != bakingLock)
    		bakingLock.lock();
        System.out.println(Thread.currentThread().getName() + " starts baking");
	        int mixture = getMixture();
	        if(mixture > 0){
	        	bake(mixture);
	        }
        System.out.println(Thread.currentThread().getName() + " completes baking");
        if(null != bakingLock)
        	bakingLock.unlock();
    }
 
    private void bake(int mixture){
    	try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Get Mixture from all the containers in parallel
     * @return
     */
    private int getMixture(){
    	try {
            Set<Future<Integer>> tasks = new HashSet<Future<Integer>>();
            ExecutorService pool = Executors.newFixedThreadPool(containers.size());
            Iterator<Ingredient> itr = cookie.getIngredients().keySet().iterator();
            while (itr.hasNext()) {
                final Ingredient ingredient = itr.next();
                tasks.add(pool.submit(new Callable<Integer> () {
                    @Override
                    public Integer call() throws Exception {
                        return getMaterial(ingredient);
                    }
                }));
            }
            Iterator<Future<Integer>> fuItr = tasks.iterator();

            int mixture = 0;
            while (fuItr.hasNext()) {
                Future<Integer> f = fuItr.next();
                mixture += f.get();
            }
            
            System.out.println(Thread.currentThread().getName() + " :: Mixture is ready to bake " + cookie.name);
            pool.shutdown();
            return mixture;
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return 0;
    }
    
    /**
     * Take material from a container. Wait if container is empty.
     * @param ingredient
     * @return
     * @throws Exception
     * @throws InterruptedException
     */
    private Integer getMaterial(final Ingredient ingredient) throws InterruptedException, Exception {
        IngredientContainer container = containers.get(ingredient);
        container.lock();
        System.out.println(Thread.currentThread().getName() + " :: Taking "+ ingredient.toString() + " from " + container.TYPE + " container");
        
        int quantity = cookie.getIngredients().get(ingredient);
        System.out.println(Thread.currentThread().getName() + " :: Quantity require:" + quantity);
        while (!container.getIngredient(quantity)) {
            container.empty.await();
            //Thread.sleep(500); //Uncomment me to create deadlock
        }
        System.out.println(Thread.currentThread().getName() + " :: Taken  " + ingredient.toString());
        container.unlock();
        return quantity;
 
    }



	

}
