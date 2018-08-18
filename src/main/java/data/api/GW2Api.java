package data.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import structures.Build;
import structures.Item;
import structures.Recipe;

import java.util.List;

public interface GW2Api {

    @GET("/v2/build")
    Single<Build> getBuildVersion();

    @GET("/v2/recipes")
    Single<String> getRecipeIds();

    @GET("/v2/items")
    Single<String> getItemIds();

    @GET("/v2/recipes")
    Single<List<Recipe>> getRecipes(@Query(value = "ids", encoded = true) String ids);

    @GET("/v2/items")
    Single<List<Item>> getItems(@Query(value = "ids", encoded = true) String ids);

}
