package data.database;

import structures.Recipe;

import java.util.List;
import java.util.Map;

public interface BaseDatabaseTool {

    void setDatabaseBuildVersion(int version);
    int getDatabaseBuildVersion();
    Recipe getRecipeForId(int id);
    void putRecipe(Recipe recipe);
    void putRecipes(List<Recipe> recipes);
    void putItem(int id, String name);
    void putItems(Map<Integer, String> items);
    String getItemNameForId(int id);

}
