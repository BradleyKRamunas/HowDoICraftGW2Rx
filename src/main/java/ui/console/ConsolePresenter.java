package ui.console;


import data.api.ApiClient;
import data.api.BaseApiClient;
import data.database.BaseDatabaseTool;
import data.database.DatabaseTool;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import structures.NamedIngredient;
import structures.TreeNode;
import structures.TreeTool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConsolePresenter implements ConsoleContract.Presenter {

    private ConsoleContract.View view;
    private BaseDatabaseTool databaseTool;
    private BaseApiClient apiClient;

    public ConsolePresenter(ConsoleContract.View view) {
        this.view = view;
        this.databaseTool = DatabaseTool.getInstance();
        this.apiClient = new ApiClient("https://api.guildwars2.com");
    }

    @Override
    public void onStart(Observer<String> progressObserver) {
        // check to see if database is out of date
        int localDatabaseVersion = databaseTool.getDatabaseBuildVersion().blockingGet();
        int remoteDatabaseVersion = apiClient.getBuildVersion().blockingGet();

        progressObserver.onNext("Local database version: " + localDatabaseVersion + ", Remote database version: " + remoteDatabaseVersion + "\n");

        // if it is, we need to populate the database
        if(localDatabaseVersion != remoteDatabaseVersion) {

            progressObserver.onNext("Must update database to continue... Fetching database.\n");

            // reset the database so old items/recipes do not interfere
            databaseTool.resetDatabase().blockingAwait();

            // first fetch all recipes from the api and store them in database
            apiClient.getRecipeIds()
                    .buffer(200)
                    .flatMap(this::stringObservableFromList)
                    .flatMap(apiClient::getRecipes)
                    .buffer(200)
                    .flatMap(recipes -> {
                        databaseTool.putRecipes(recipes).blockingAwait();
                        return Observable.just("Recipe for item id " + recipes.get(recipes.size()-1).getOutput() + " fetched");
                    }).subscribeOn(Schedulers.io())
                    .blockingSubscribe(progressObserver);

            // then fetch all the items from the api and store them in database
            apiClient.getItemIds()
                    .buffer(200)
                    .flatMap(this::stringObservableFromList)
                    .flatMap(apiClient::getItems)
                    .buffer(200)
                    .flatMap(items -> {
                        databaseTool.putItems(items).blockingAwait();
                        return Observable.just("Item " + items.get(items.size()-1).getItemName() + " fetched");
                    }).subscribeOn(Schedulers.io())
                    .blockingSubscribe(progressObserver);

            // now that we've finished, we can set the database version
            databaseTool.setDatabaseBuildVersion(remoteDatabaseVersion).blockingAwait();
        }
    }

    private Observable<String> stringObservableFromList(List<Integer> list) {
        Iterator<Integer> iterator = list.iterator();
        StringBuilder string = new StringBuilder();
        while(iterator.hasNext()) {
            int next = iterator.next();
            string.append(next);
            if(iterator.hasNext()) string.append(",");
        }
        return Observable.just(string.toString());
    }

    @Override
    public Observable<String> processRequest(String itemName, int quantity) {
        List<NamedIngredient> ingredients = new ArrayList<>();
        List<NamedIngredient> crafting = new ArrayList<>();

        // first we get the corresponding item id from the item name
        int itemId = databaseTool.getItemIdForName(itemName).blockingGet();

        // then we create our recipe tree
        TreeNode root  = TreeTool.createTree(itemId, quantity, databaseTool);

        // here we transform our tree into the ingredients we need
        Observable.just(root)
                .concatMap(node -> Observable.fromIterable(TreeTool.getIngredientRequirements(node)))
                .map(ingredient -> {
                    String name = databaseTool.getItemNameForId(ingredient.getItemId()).blockingGet();
                    ingredient.setItemName(name);
                    return ingredient;
                })
                .subscribeOn(Schedulers.io())
                .blockingSubscribe(ingredients::add, Throwable::printStackTrace);

        // here we transform our tree into the crafting order we must follow
        Observable.just(root)
                .concatMap(node -> Observable.fromIterable(TreeTool.getCraftingOrder(node)))
                .map(ingredient -> {
                    String name = databaseTool.getItemNameForId(ingredient.getItemId()).blockingGet();
                    ingredient.setItemName(name);
                    return ingredient;
                })
                .subscribeOn(Schedulers.io())
                .blockingSubscribe(crafting::add, Throwable::printStackTrace);

        StringBuilder builder = new StringBuilder();
        builder.append("Ingredients needed: \n");
        for(NamedIngredient ingredient : ingredients) {
            builder.append(ingredient.getItemName());
            builder.append(": ");
            builder.append(ingredient.getQuantity());
            builder.append("\n");
        }

        builder.append("\nCrafting Order: \n");
        for(NamedIngredient ingredient : crafting) {
            builder.append("Craft ");
            builder.append(ingredient.getQuantity());
            builder.append(" ");
            builder.append(ingredient.getItemName());
            builder.append("\n");
        }

        return Observable.just(builder.toString());
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public boolean isViewAttached() {
        return this.view != null;
    }
}
