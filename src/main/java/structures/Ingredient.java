package structures;

public class Ingredient {

    private final int itemId;
    private final int quantity;

    public Ingredient(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

}
