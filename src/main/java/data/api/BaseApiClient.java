package data.api;

import io.reactivex.Observable;
import io.reactivex.Single;
import structures.Item;
import structures.Recipe;

public interface BaseApiClient {

    Single<Integer> getBuildVersion();
    Observable<Integer> getRecipeIds();
    Observable<Integer> getItemIds();
    Observable<Recipe> getRecipes(String ids);
    Observable<Item> getItems(String ids);

}
