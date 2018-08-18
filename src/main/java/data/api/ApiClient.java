package data.api;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import structures.Item;
import structures.Recipe;

import java.util.List;

public class ApiClient implements BaseApiClient {

    private OkHttpClient client;
    private Retrofit retrofitGson;
    private Retrofit retrofitString;
    private String apiURL;

    public ApiClient(String apiURL) {
        this.apiURL = apiURL;
        this.client = new OkHttpClient.Builder().build();
        this.retrofitGson = new Retrofit.Builder().baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build();
        this.retrofitString = new Retrofit.Builder().baseUrl(apiURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build();
    }

    @Override
    public Single<Integer> getBuildVersion() {
        GW2Api connection = retrofitGson.create(GW2Api.class);
        return Single.just(connection.getBuildVersion().blockingGet().getBuildNumber());
    }

    @Override
    public Observable<Integer> getRecipeIds() {
        GW2Api connection = retrofitString.create(GW2Api.class);
        String json = connection.getRecipeIds().blockingGet();
        json = json.substring(1, json.length()-1).trim().replaceAll("\\s+", "");
        return Observable.fromArray(json.split(",")).map(Integer::parseInt);
    }

    @Override
    public Observable<Integer> getItemIds() {
        GW2Api connection = retrofitString.create(GW2Api.class);
        String json = connection.getItemIds().blockingGet();
        json = json.substring(1, json.length()-1).trim().replaceAll("\\s+", "");
        return Observable.fromArray(json.split(",")).map(Integer::parseInt);
    }

    @Override
    public Observable<Recipe> getRecipes(String ids) {
        GW2Api connection = retrofitGson.create(GW2Api.class);
        List<Recipe> recipes = connection.getRecipes(ids).blockingGet();
        return Observable.fromIterable(recipes);
    }

    @Override
    public Observable<Item> getItems(String ids) {
        GW2Api connection = retrofitGson.create(GW2Api.class);
        List<Item> items = connection.getItems(ids).blockingGet();
        return Observable.fromIterable(items);
    }
}
