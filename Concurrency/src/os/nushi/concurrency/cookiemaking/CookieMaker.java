package os.nushi.concurrency.cookiemaking;

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
 
public class CookieMaker implements Runnable {
    EnumMap < Ingredient, IngredientContainer > containers;
    int containerCapacity = 0; //How many containers a maker can have
    Semaphore bakingCapacity; //How many cookies a maker can bake
    EnumMap < Ingredient, Integer > cookie;
    private Lock bakingLock = new ReentrantLock();
    private Lock containerLock = new ReentrantLock();
    Filler fillingWorker = new Filler(6, containerLock);
 
    public void addContainer(Ingredient i, IngredientContainer c) throws Exception {
        //System.out.println("Adding " + i.toString() + " container.");
        if (containers.size() == containerCapacity) {
            throw new Exception("Containers over loaded");
        }
        c.setEmptyCondition(containerLock.newCondition());
        //System.out.println("Condition is added to Container");
        fillingWorker.addContainer(c);
        //System.out.println("Container is added to Filler");
        containers.put(i, c);
        System.out.println(i.toString() + "container added");
    }
 
    public void StartBaking() {
        try {
            System.out.println(bakingCapacity.getQueueLength() + " are waiting");
            bakingCapacity.acquire();
            System.out.println(bakingCapacity.availablePermits() + " more threads can enter");
            bakingLock.lock();
            System.out.println(Thread.currentThread().getName() + " :: Entered.");
            Set < Ingredient > ingredients = cookie.keySet();
            Iterator < Ingredient > itr = ingredients.iterator();
            ExecutorService pool;
            Set < Future < Boolean >> tasks = new HashSet < Future < Boolean >> ();
 
            try {
                pool = Executors.newFixedThreadPool(containers.size());
                while (itr.hasNext()) {
                    System.out.println();
                    final Ingredient ingredient = itr.next();
                    tasks.add(pool.submit(new Callable < Boolean > () {@
                        Override
                        public Boolean call() throws Exception {
                            return getIngredient(ingredient);
                        }
                    }));
                }
                Iterator < Future < Boolean >> fuItr = tasks.iterator();
                boolean tasksCompleted = true;
 
                while (fuItr.hasNext()) {
                    Future < Boolean > f = fuItr.next();
                    tasksCompleted &= f.get();
                }
                if (tasksCompleted) {
                    System.out.println("Mixture is ready. Backing cookie");
                    pool.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
 
            System.out.println("Thread " + Thread.currentThread().getName() + " :: completed");
            bakingLock.unlock();
            bakingCapacity.release(); //let other threads start baking
            fillingWorker.stopFilling();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
 
    /**
     * Take ingredients from a container. Wait if container is empty.
     * @param ingredient
     * @return
     * @throws Exception
     * @throws InterruptedException
     */
    private boolean getIngredient(final Ingredient ingredient) throws InterruptedException, Exception {
        containerLock.lock();
        System.out.println("\t\tGet ingredient: " + ingredient.toString());
        IngredientContainer container = containers.get(ingredient);
        System.out.println(ingredient.toString() + " Container has " + container.getQuantityHeld());
        int quantity = cookie.get(ingredient);
        System.out.println("\t\tQuantity require:" + quantity);
        while (!container.getIngredient(quantity)) {
            container.empty.await();
        }
        //In real world I believe, this method will take some time to take ingredients from a container.
        TimeUnit.SECONDS.sleep(5);
        System.out.println("\t\tingredient:  " + ingredient.toString() + " is taken.");
        containerLock.unlock();
        return true;
 
    }
 
    public static void main(String[] args) {
       
        CookieMaker cm = new CookieMaker(4, 5);
        EnumMap < Ingredient, Integer > chocoWheatBar = new EnumMap < Ingredient, Integer > (Ingredient.class);
 
        chocoWheatBar.put(Ingredient.ChokoPowder, 3);
        chocoWheatBar.put(Ingredient.WheatPowder, 1);
 
        try {
            cm.addContainer(Ingredient.ChokoPowder, new IngredientContainer(12));
            cm.addContainer(Ingredient.WheatPowder, new IngredientContainer(15));
            cm.setCookie(chocoWheatBar);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
 
        new Thread(cm, "maker1").start();
        new Thread(cm, "maker2").start();
        new Thread(cm, "maker3").start();
        new Thread(cm, "maker4").start();
        new Thread(cm, "maker5").start();
        new Thread(cm, "maker6").start();
 
        cm.fillingWorker.startFilling();
 
    }
 
    @Override
    public void run() {
        if (checkSetup()) {
            StartBaking();
        } else {
            System.out.println("Initial setup is required");
        }
    }
 
    public CookieMaker() {
        containers = new EnumMap < Ingredient, IngredientContainer > (Ingredient.class);
    }
 
    public CookieMaker(int bc) {
        bakingCapacity = new Semaphore(bc);
        containers = new EnumMap < Ingredient, IngredientContainer > (Ingredient.class);
    }
 
    /**
     * 
     * @param bc Baking capacity
     * @param cc Number of containers
     */
    public CookieMaker(int bc, int cc) {
    	System.out.println("Baking capacity:" + bc);
        System.out.println("Number of containers :" + cc);
        bakingCapacity = new Semaphore(bc);
        this.containerCapacity = cc;
        containers = new EnumMap < Ingredient, IngredientContainer > (Ingredient.class);
    }
 
    public void setCookie(EnumMap < Ingredient, Integer > cookie) {
        this.cookie = cookie;
    }
 
    public void setBakingCapacity(int bakingCapacity) {
        this.bakingCapacity = new Semaphore(bakingCapacity);
    }
 
    public void setContainerCapacity(int containerCapacity) {
        this.containerCapacity = containerCapacity;
    }
 
    /**
     * Checks Maker's initial set up.
     * @throws Exception
     */
    public boolean checkSetup() {
        boolean signal = false;
        /*if(bakingCapacity. < 1){
        System.out.println("Maker can not bake cookies");
        }
        else*/
        if (containerCapacity < 1) {
            System.out.println("Container capacity is 0");
        } else if (containers.size() < 1) {
            System.out.println("No container is installed.");
        } else {
            signal = true;
        }
        return signal;
    }
}