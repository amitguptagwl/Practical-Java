package os.nushi.concurrency.cookiemaking;

import java.util.EnumMap;

public class Cookie {
    EnumMap < Ingredient, Integer > ingredients;
 
    Cookie() {
        ingredients = new EnumMap < Ingredient, Integer > (Ingredient.class);
    }
 
    public void setIngredient(Ingredient i, int quantity) {
        ingredients.put(i, quantity);
    }
 
    public EnumMap < Ingredient, Integer > getIngredients() {
        return ingredients;
    }
}
