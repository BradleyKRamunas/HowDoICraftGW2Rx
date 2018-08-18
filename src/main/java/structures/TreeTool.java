package structures;

import data.database.BaseDatabaseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TreeTool {

    /**
     * Creates a Recipe tree that can then be used to determine crafting order and required ingredients to craft the root.
     * @param rootId item id of the item to be crafted
     * @param rootQuant quantity of the item to be crafted
     * @param databaseTool tool that can be queried to get recipes for items
     * @return root of the tree
     */
    public static TreeNode createTree(int rootId, int rootQuant, BaseDatabaseTool databaseTool) {
        TreeNode root = new TreeNode(rootId, rootQuant);
        populateChildren(root, databaseTool);
        return root;
    }

    /**
     * Recursively populate the Recipe tree
     * @param node current node being worked on
     * @param databaseTool tool that can be queried to get recipes for items
     */
    private static void populateChildren(TreeNode node, BaseDatabaseTool databaseTool) {
        Recipe recipe = databaseTool.getRecipeForId(node.getItemId()).blockingGet();
        if(recipe != Recipe.EMPTY) {
            List<Ingredient> inputs = recipe.getInputs();
            List<TreeNode> children = new ArrayList<>();
            for(Ingredient ingredient : inputs) children.add(new TreeNode(ingredient));
            node.setChildren(children);
            for(TreeNode child : node.getChildren()) {
                populateChildren(child, databaseTool);
            }
        }
    }

    /**
     * Creates a list of Ingredients with the required amount of each ingredient in order to craft the item
     * @param root the item to be crafted
     * @return list of Ingredients with correct quantities and item ids
     */
    public static List<NamedIngredient> getIngredientRequirements(TreeNode root) {
        Map<Integer, Integer> idToQuantityMap = new TreeMap<>();
        populateIngredientRequirements(root, idToQuantityMap, 1);
        List<NamedIngredient> ingredients = new ArrayList<>();
        for(Integer key : idToQuantityMap.keySet()) {
            ingredients.add(new NamedIngredient(key, idToQuantityMap.get(key)));
        }
        return ingredients;
    }

    /**
     * Recursively traverse the Recipe tree to determine ingredient requirements
     * @param node current node being worked on
     * @param ingredients current mapping of item id to quantity
     * @param multiplier current tree-level in terms of the number of parent that needs to be made
     */
    private static void populateIngredientRequirements(TreeNode node, Map<Integer, Integer> ingredients, int multiplier) {
        int newMultiplier = multiplier * node.getQuantity();
        if(isLeaf(node)) {
            ingredients.merge(node.getItemId(), newMultiplier, Integer::sum);
        } else {
            for(TreeNode child : node.getChildren()) {
                populateIngredientRequirements(child, ingredients, newMultiplier);
            }
        }
    }

    /**
     * Check to see if the node passed in has no children
     * @param node to be checked
     * @return true if has no children, false otherwise
     */
    private static boolean isLeaf(TreeNode node) {
        return node.getChildren().isEmpty();
    }

    /**
     * Get the crafting order of ingredients to be shown to the user
     * @param root to be crafted
     * @return an ordered list of crafting steps determined by the ingredient (item and quantity)
     */
    public static List<NamedIngredient> getCraftingOrder(TreeNode root) {
        List<NamedIngredient> order = new ArrayList<>();
        populateCraftingOrder(root, order, 1);
        return order;
    }

    /**
     * Recursively traverse the tree in order to populate the crafting order list
     * @param node current node being worked on
     * @param order current order of ingredients to be crafted
     * @param multiplier current tree-level in terms of the number of parent that needs to be made
     */
    private static void populateCraftingOrder(TreeNode node, List<NamedIngredient> order, int multiplier) {
        if(!isLeaf(node)) {
            int newMultiplier = multiplier * node.getQuantity();
            if(isParentOfLeaves(node)) {
                order.add(new NamedIngredient(node.getItemId(), newMultiplier));
            } else {
                for(TreeNode child : node.getChildren()) {
                    populateCraftingOrder(child, order, newMultiplier);
                }
                order.add(new NamedIngredient(node.getItemId(), newMultiplier));
            }
        }
    }

    /**
     * Check to see if the node's item is made strictly from leaf nodes (i.e. items that are crafted only from base ingredients)
     * @param node to be checked
     * @return true if the node is a parent of only leaves, false otherwise
     */
    private static boolean isParentOfLeaves(TreeNode node) {
        if(isLeaf(node)) return false;
        for(TreeNode child : node.getChildren()) {
            if(!isLeaf(child)) return false;
        }
        return true;
    }


}
