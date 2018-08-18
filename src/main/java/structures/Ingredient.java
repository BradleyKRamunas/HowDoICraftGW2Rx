package structures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient {

    @Expose
    @SerializedName("item_id")
    private final int itemId;

    @Expose
    @SerializedName("count")
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
