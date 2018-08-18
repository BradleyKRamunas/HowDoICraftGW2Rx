import data.database.BaseDatabaseTool;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import structures.*;

import java.util.List;

public class TreeToolTest {

    /**
     * The tree structure is as follows:
     *                 (A,2)
     *                /  |  \
     *        (B,2)   (C, 4)  (D, 1)
     *          |      /  \
     *       (D, 2) (E, 8) (F, 2)
     *                 |
     *              (D, 1)
     * A = 1, B = 2, C = 3, D = 4, E = 5, F = 6 (name mapping)
     * Resulting mapping should be:
     * D: 74
     * F: 16
     */

    private TreeNode root;
    private static final BaseDatabaseTool mockDatabase;
    static {
        Recipe recipeA = new Recipe(1, 2);
        Recipe recipeB = new Recipe(2, 2);
        Recipe recipeC = new Recipe(3, 4);
        Recipe recipeE = new Recipe(5, 8);
        Ingredient ingredientAB = new Ingredient(2, 2);
        Ingredient ingredientAC = new Ingredient(3, 4);
        Ingredient ingredientAD = new Ingredient(4, 1);
        Ingredient ingredientBD = new Ingredient(4, 2);
        Ingredient ingredientCE = new Ingredient(5, 8);
        Ingredient ingredientCF = new Ingredient(6, 2);
        Ingredient ingredientED = new Ingredient(4, 1);
        recipeA.addIngredient(ingredientAB);
        recipeA.addIngredient(ingredientAC);
        recipeA.addIngredient(ingredientAD);
        recipeB.addIngredient(ingredientBD);
        recipeC.addIngredient(ingredientCE);
        recipeC.addIngredient(ingredientCF);
        recipeE.addIngredient(ingredientED);
        mockDatabase = new BaseDatabaseTool() {
            @Override
            public void setupDatabase() {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Completable resetDatabase() {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Completable setDatabaseBuildVersion(int version) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Single<Integer> getDatabaseBuildVersion() {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Single<Recipe> getRecipeForId(int id) {
                Recipe requestedRecipe;
                switch(id) {
                    case 1: // A
                        requestedRecipe = recipeA;
                        break;
                    case 2: // B
                        requestedRecipe = recipeB;
                        break;
                    case 3: // C
                        requestedRecipe = recipeC;
                        break;
                    case 4: // D
                        requestedRecipe = Recipe.EMPTY;
                        break;
                    case 5: // E
                        requestedRecipe = recipeE;
                        break;
                    case 6: // F
                        requestedRecipe = Recipe.EMPTY;
                        break;
                    default:
                        requestedRecipe = Recipe.EMPTY;
                        break;
                }
                return Single.just(requestedRecipe);
            }

            @Override
            public Completable putRecipe(Recipe recipe) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Completable putRecipes(List<Recipe> recipes) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Completable putItem(int id, String name) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Completable putItems(List<Item> items) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Single<String> getItemNameForId(int id) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }

            @Override
            public Single<Integer> getItemIdForName(String name) {
                throw new UnsupportedOperationException("This function is not available for TreeToolTest.");
            }
        };
    }

    @Before
    public void setUp() {
        this.root = TreeTool.createTree(1, 2, mockDatabase);
    }

    @Test
    public void testCreateTree() {
        // Checking root node correctness
        Assert.assertEquals(1, root.getItemId());
        Assert.assertEquals(2, root.getQuantity());
        Assert.assertEquals(3, root.getChildren().size());
        Assert.assertEquals(2, root.getChildren().get(0).getItemId());
        Assert.assertEquals(2, root.getChildren().get(0).getQuantity());
        Assert.assertEquals(3, root.getChildren().get(1).getItemId());
        Assert.assertEquals(4, root.getChildren().get(1).getQuantity());
        Assert.assertEquals(4, root.getChildren().get(2).getItemId());
        Assert.assertEquals(1, root.getChildren().get(2).getQuantity());

        // Checking (B, 2) node correctness
        TreeNode curr = root.getChildren().get(0);
        Assert.assertEquals(1, curr.getChildren().size());
        Assert.assertEquals(4, curr.getChildren().get(0).getItemId());
        Assert.assertEquals(2, curr.getChildren().get(0).getQuantity());

        // Checking (D, 2) node correctness
        curr = curr.getChildren().get(0);
        Assert.assertEquals(0, curr.getChildren().size());

        // Checking (C, 4) node correctness
        curr = root.getChildren().get(1);
        Assert.assertEquals(2, curr.getChildren().size());
        Assert.assertEquals(5, curr.getChildren().get(0).getItemId());
        Assert.assertEquals(8, curr.getChildren().get(0).getQuantity());
        Assert.assertEquals(6, curr.getChildren().get(1).getItemId());
        Assert.assertEquals(2, curr.getChildren().get(1).getQuantity());

        // Checking (E, 8) node correctness
        curr = curr.getChildren().get(0);
        Assert.assertEquals(1, curr.getChildren().size());
        Assert.assertEquals(4, curr.getChildren().get(0).getItemId());
        Assert.assertEquals(1, curr.getChildren().get(0).getQuantity());

        // Checking (D, 1) node correctness
        curr = curr.getChildren().get(0);
        Assert.assertEquals(0, curr.getChildren().size());

        // Checking (F, 2) node correctness
        curr = root.getChildren().get(1).getChildren().get(1);
        Assert.assertEquals(0, curr.getChildren().size());

        // Checking (D, 1) node correctness
        curr = root.getChildren().get(2);
        Assert.assertEquals(0, curr.getChildren().size());

    }

    @Test
    public void testIngredientRequirement() {
        // Expected ingredients: (4, 74), (6, 16)
        List<NamedIngredient> ingredients = TreeTool.getIngredientRequirements(root);
        Assert.assertEquals(2, ingredients.size());
        NamedIngredient ingredientOne = ingredients.get(0);
        NamedIngredient ingredientTwo = ingredients.get(1);
        Assert.assertTrue(ingredientOne.getItemId() == 4 || ingredientOne.getItemId() == 6);
        Assert.assertTrue(ingredientTwo.getItemId() == 4 || ingredientTwo.getItemId() == 6);
        if(ingredientOne.getItemId() == 4) {
            Assert.assertEquals(74, ingredientOne.getQuantity());
            Assert.assertEquals(16, ingredientTwo.getQuantity());
        } else {
            Assert.assertEquals(16, ingredientOne.getQuantity());
            Assert.assertEquals(74, ingredientTwo.getQuantity());
        }
    }

    @Test
    public void testCraftingOrder() {
        // Expected order: (2, 4), (5, 64), (3, 8), (1, 2)
        List<NamedIngredient> ingredients = TreeTool.getCraftingOrder(root);
        Assert.assertEquals(4, ingredients.size());
        Assert.assertEquals(2, ingredients.get(0).getItemId());
        Assert.assertEquals(4, ingredients.get(0).getQuantity());
        Assert.assertEquals(5, ingredients.get(1).getItemId());
        Assert.assertEquals(64, ingredients.get(1).getQuantity());
        Assert.assertEquals(3, ingredients.get(2).getItemId());
        Assert.assertEquals(8, ingredients.get(2).getQuantity());
        Assert.assertEquals(1, ingredients.get(3).getItemId());
        Assert.assertEquals(2, ingredients.get(3).getQuantity());
    }
}
