package os.nushi.concurrency.cookiemaking2;

import os.nushi.concurrency.tracer.Tracer;

//DEBUG: jstack PID | grep tid= | grep -v daemon
public class CookieMakerStarter {
	 
    public static void main(String[] args) throws Exception {
       
    	Tracer.startDeadLockMonitor();
    	Tracer.trace();
    	
        CookieMaker cm = new CookieMaker();
        
        //Add cookie recipe
        Cookie chocoWheatBar = new Cookie("Choco-Wheat Bar");
        chocoWheatBar.setIngredient(Ingredient.ChokoPowder, 3);
        chocoWheatBar.setIngredient(Ingredient.WheatPowder, 1);
        cm.setCookie(chocoWheatBar);
 
        //Add required containers 
        
        IngredientContainer chokoPowderContainer = new IngredientContainer(Ingredient.ChokoPowder,12);
        IngredientContainer wheatPowderContainer = new IngredientContainer(Ingredient.WheatPowder,15);
        
        Filler fillingWorker = new Filler();
        fillingWorker.addContainer(chokoPowderContainer);
        
        fillingWorker.addContainer(wheatPowderContainer);
        fillingWorker.start(); //Comment it to create live lock

        cm.addContainer(chokoPowderContainer);
        cm.addContainer(wheatPowderContainer);
        
        cm.enableParallelBaking(true);
        cm.maxParallelBakingCapacity(3);
        //Let's bake 8 cookies in parallel
        for(int i=0; i<8;i++ ){
        	cm.startBaking();
        }
        
        
        
    }
 
}
