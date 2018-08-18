package data.database;

import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.Single;
import structures.Item;
import structures.Recipe;

import java.sql.*;
import java.util.List;

public class DatabaseTool implements BaseDatabaseTool {

    private static Connection connection;
    private static Gson gson;
    private static BaseDatabaseTool instance;

    public static BaseDatabaseTool getInstance() {
        if(connection == null) init();
        if(gson == null) gson = new Gson();
        return instance;
    }

    private DatabaseTool() {
    }

    private static void init() {
        instance = new DatabaseTool();
        gson = new Gson();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:gw2RecipesAndItems.db");
            instance.setupDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database.\n" + e.getMessage());
        }
    }

    @Override
    public void setupDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS recipes (item_id integer, item_recipe string)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS items (item_id integer, item_name string)");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to setup the database.\n" + e.getMessage());
        }
    }

    @Override
    public Completable resetDatabase() {
        return Completable.fromAction(() -> {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS recipes");
            statement.execute("DROP TABLE IF EXISTS items");
            statement.close();
            setupDatabase();
        });
    }

    @Override
    public Completable setDatabaseBuildVersion(int version) {
        return Completable.fromAction(() -> {
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA user_version = " + version);
            statement.close();
        });
    }

    @Override
    public Single<Integer> getDatabaseBuildVersion() {
        return Single.fromCallable(() -> {
           Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery("PRAGMA user_version");
           int version = resultSet.getInt(1);
           resultSet.close();
           statement.close();
           return version;
        });
    }

    @Override
    public Single<Recipe> getRecipeForId(int id) {
        return Single.fromCallable(() -> {
           Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery("SELECT * FROM recipes WHERE item_id = " + id + " LIMIT 1");
           resultSet.next();
           Recipe recipe = Recipe.EMPTY;
           if(!resultSet.isAfterLast()) {
               String json = resultSet.getString("item_recipe");
               recipe = gson.fromJson(json, Recipe.class);
           }
           resultSet.close();
           statement.close();
           return recipe;
        });
    }

    @Override
    public Completable putRecipe(Recipe recipe) {
        return Completable.fromAction(() -> {
            Statement statement = connection.createStatement();
            String json = gson.toJson(recipe);
            statement.executeUpdate("INSERT INTO recipes VALUES (" + recipe.getOutput() + ", '" + json + "')");
            statement.close();
        });
    }

    @Override
    public Completable putRecipes(List<Recipe> recipes) {
        return Completable.fromAction(() -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO recipes VALUES (?, ?)");
            for(Recipe recipe : recipes) {
                preparedStatement.setInt(1, recipe.getOutput());
                preparedStatement.setString(2, gson.toJson(recipe));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        });
    }

    @Override
    public Completable putItem(int id, String name) {
        return Completable.fromAction(() -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO items VALUES (" + id + ", '" + name + "')");
            statement.close();
        });
    }

    @Override
    public Completable putItems(List<Item> items) {
        return Completable.fromAction(() -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items VALUES (?, ?)");
            for(Item item : items) {
                preparedStatement.setInt(1, item.getItemId());
                preparedStatement.setString(2, item.getItemName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        });
    }

    @Override
    public Single<String> getItemNameForId(int id) {
        return Single.fromCallable(() -> {
           Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery("SELECT * FROM items WHERE item_id = " + id + " LIMIT 1");
           resultSet.next();
           String itemName = "null";
           if(!resultSet.isAfterLast()) {
               itemName = resultSet.getString("item_name");
           }
           resultSet.close();
           statement.close();
           return itemName;
        });
    }

    @Override
    public Single<Integer> getItemIdForName(String name) {
        return Single.fromCallable(() -> {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM items WHERE item_name = '" + name + "' LIMIT 1");
            resultSet.next();
            int id = -1;
            if(!resultSet.isAfterLast()) {
                id = resultSet.getInt("item_id");
            }
            resultSet.close();
            statement.close();
            return id;
        });
    }
}
