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
package os.nushi.concurrency.cookiemaking2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IngredientContainer {
	final Ingredient TYPE;
    final int CAPACITY; //How much quantity of an ingredient a container can have
    private int quantityHeld;
    Condition empty;
    private Lock lock;
 
    IngredientContainer(Ingredient ingredient, int capacity ) {
    	TYPE = ingredient;
        this.CAPACITY = capacity ;
        lock = new ReentrantLock();
        empty = lock.newCondition();
    }

    public void lock(){
    	lock.lock();
    }
    
    public void unlock(){
    	lock.unlock();
    }
   
    public int getQuantityRequired() {
        return CAPACITY - quantityHeld;
    }
 
    public void fill(int n) throws Exception {
        if ((n + quantityHeld) > CAPACITY) {
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
        int require = CAPACITY - quantityHeld;
        int toBeFilled = Math.min(require, n);
        quantityHeld += toBeFilled;
        return toBeFilled;
    }
 
    //Multiple threads can access it at a time
    public boolean getIngredient(int n) throws Exception {
    	System.out.println(TYPE.toString() + " Container has " + getQuantityHeld() + " only." );
        
    	if (n > CAPACITY) throw new Exception("Accessing quantity more than capacity");
    	lock();
        if (quantityHeld >= n) {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(Thread.currentThread().getName() + " :: Taking " + n  );
            quantityHeld -= n;
            unlock();
            return true;
        }
        unlock();
        //System.out.println("Less Quantity Held");
        return false;
    }

	public int getCapacity() {
		return CAPACITY;
	}

	public int getQuantityHeld() {
		return quantityHeld;
	}
}