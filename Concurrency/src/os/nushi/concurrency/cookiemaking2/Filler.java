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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
 
/*
* Filler checks containers on regular interval and fill them if they are empty.
*
* @author Amit Gupta
*/
public class Filler extends Thread{
    private ArrayList < IngredientContainer > ingredientContainers;
    private int checkInterval = 2;
    private int fillingQuantity = 2;
    private boolean isInterrupted = false;
 
    public Filler() {
        ingredientContainers = new ArrayList<IngredientContainer>();
        setDaemon(true);
    }
 
    public void addContainer(IngredientContainer c) throws Exception {
        ingredientContainers.add(c);
    }

    @Override
    public void run() {
    	System.out.println("Filler has started working");
        while (!isInterrupted) {
            try {
				TimeUnit.SECONDS.sleep(checkInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            System.out.println("Filler is checking containers");
            Iterator<IngredientContainer> itr = ingredientContainers.iterator();
            while (itr.hasNext()) {
                IngredientContainer container = itr.next();
                container.lock();
                if(container.getQuantityRequired() > 0){
	                System.out.println(container.TYPE + " Require : " + container.getQuantityRequired() 
	                		+ " Capacity : " + container.getCapacity()
	                		+ " Filling : " + fillingQuantity
	                    		);
	                
	                    int filledQ = container.fillSafe(fillingQuantity); //Try to fill required quantity only.
	                System.out.println("Filled " + filledQ);
	                container.empty.signalAll(); //This condition must be instantiate from CookieMaker
                }
                container.unlock();
            }
        }
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