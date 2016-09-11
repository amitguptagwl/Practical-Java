package os.nushi.concurrency.cookiemaking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
 
/*
* Filler checks a Container with regular interval. Either it fills Container
* safely or check it before filling it.
*
* @author Amit Gupta
*/
public class Filler {
    private ArrayList < IngredientContainer > ingredientContainers;
    int capacity = 6;
    private int checkInterval = 3;
    private int fillingQuantity = 2;
    private boolean isInterrupted = false;
    private Lock containerLock;
 
    public Filler(int c, Lock l) {
        ingredientContainers = new ArrayList < IngredientContainer > ();
        capacity = c;
        containerLock = l;
    }
 
    public void addContainer(IngredientContainer c) throws Exception {
        if (ingredientContainers.size() == capacity)
            throw new Exception("Filler is overloaded");
        ingredientContainers.add(c);
    }
    /**
     * Filler checks container with regular interval and fills if it is empty.
     * @author Amit Gupta
     */
    public void startFilling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Filler has started working");
                while (!isInterrupted) {
                    try {
                        TimeUnit.SECONDS.sleep(checkInterval);
                        System.out.println("Filler starts checking containers");
                        containerLock.lock();
                        Iterator <IngredientContainer> itr = ingredientContainers.iterator();
                        while (itr.hasNext()) {
 
                            IngredientContainer ingredientContainer = itr.next();
                            System.out.println("Require : " + ingredientContainer.getQuantityRequired());
                            System.out.println("Capacity : " + ingredientContainer.getCapacity());
                            System.out.println("Filling : " + fillingQuantity);
 
                            int filledQ = ingredientContainer.fillSafe(fillingQuantity); //Try to fill required quantity only.
                            System.out.println("Filled " + filledQ);
                            ingredientContainer.empty.signalAll(); //This condition must be instantiate from CookieMaker
 
                        }
                        containerLock.unlock();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
    }
 
    public void stopFilling() {
        isInterrupted = true;
    }
    /**
     * How long Filler should wait checking a container.
     * @param checkInterval Seconds. default is 3 seconds.
     */
    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }
 
    /**
     * How much quantity should be filled in a container, if it is empty.
     * @param fillingQuantity default 10.
     */
    public void setFillingQuantity(int fillingQuantity) {
        this.fillingQuantity = fillingQuantity;
    }
}