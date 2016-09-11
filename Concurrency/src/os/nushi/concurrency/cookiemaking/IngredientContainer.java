package os.nushi.concurrency.cookiemaking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class IngredientContainer {
    private int capacity; //How much quantity of an ingredient a container can have
    private int quantityHeld;
    Condition empty;
 
    IngredientContainer(int c) {
        capacity = c;
    }
 
    //getters
    
 
    public int getQuantityRequired() {
        return capacity - quantityHeld;
    }
 
    public void fill(int n) throws Exception {
        if ((n + quantityHeld) > capacity) {
            throw new Exception("Overfilled");
        }
        quantityHeld += n;
    }
 
    public boolean isEmpty() {
        return quantityHeld == 0;
    }
    /**
     *
     * @param n filled units
     * @return
     */
    public int fillSafe(int n) {
        int require = capacity - quantityHeld;
        int toBeFilled = Math.min(require, n);
        quantityHeld += toBeFilled;
        return toBeFilled;
    }
 
    public void setEmptyCondition(Condition c) {
        this.empty = c;
    }
    public boolean getIngredient(int n) throws Exception {
        if (n > capacity) {
            throw new Exception("Accessing quantity more than capacity");
        }
        if (quantityHeld >= n) {
            TimeUnit.SECONDS.sleep(1);
            quantityHeld -= n;
            return true;
        }
 
        System.out.println("Less Quantity Held");
        return false;
    }

	public int getCapacity() {
		return capacity;
	}

	public int getQuantityHeld() {
		return quantityHeld;
	}
}