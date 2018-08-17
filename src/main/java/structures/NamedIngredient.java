package structures;

public class NamedIngredient extends Ingredient {

    private String itemName;

    public NamedIngredient(int itemId, int quantity) {
        super(itemId, quantity);
    }

    public void setItemName(String name) {
        itemName = name;
    }

    public String getItemName() {
        return itemName;
    }
}
