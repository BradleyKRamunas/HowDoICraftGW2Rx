package data.database;

import io.reactivex.Completable;
import io.reactivex.Single;
import structures.Item;
import structures.Recipe;

import java.util.List;

public interface BaseDatabaseTool {

    void setupDatabase();
    Completable resetDatabase();
    Completable setDatabaseBuildVersion(int version);
    Single<Integer> getDatabaseBuildVersion();
    Single<Recipe> getRecipeForId(int id);
    Completable putRecipe(Recipe recipe);
    Completable putRecipes(List<Recipe> recipes);
    Completable putItem(int id, String name);
    Completable putItems(List<Item> items);
    Single<String> getItemNameForId(int id);
    Single<Integer> getItemIdForName(String name);

}
