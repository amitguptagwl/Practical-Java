package os.nushi.concurrency.cookiemaking2;

import java.util.EnumMap;

public class Cookie {
	final String name;
    EnumMap < Ingredient, Integer > ingredients;
 
    Cookie(String name) {
        this.name = name;
		ingredients = new EnumMap < Ingredient, Integer > (Ingredient.class);
    }
 
    public void setIngredient(Ingredient i, int quantity) {
        ingredients.put(i, quantity);
    }
 
    public EnumMap < Ingredient, Integer > getIngredients() {
        return ingredients;
    }
}
