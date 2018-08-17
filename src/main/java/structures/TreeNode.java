package structures;


import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private int itemId;
    private int quantity;
    private List<TreeNode> children;

    public TreeNode(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.children = new ArrayList<>();
    }

    public TreeNode(Ingredient ingredient) {
        this.itemId = ingredient.getItemId();
        this.quantity = ingredient.getQuantity();
        this.children = new ArrayList<>();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

}
