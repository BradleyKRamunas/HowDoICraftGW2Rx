package structures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @Expose
    @SerializedName("id")
    private int itemId;

    @Expose
    @SerializedName("name")
    private String itemName;

    public Item(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
