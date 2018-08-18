package structures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Recipe {

    public static final Recipe EMPTY = new Recipe(-1, -1);

    @Expose
    @SerializedName("output_item_id")
    private int output;

    @Expose
    @SerializedName("output_item_count")
    private int quantity;

    @Expose
    @SerializedName("ingredients")
    private List<Ingredient> inputs;

    public Recipe(int output, int quantity) {
        this.output = output;
        this.quantity = quantity;
        inputs = new ArrayList<>();
    }

    public int getOutput() {
        return output;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    public void setInputs(List<Ingredient> ingredients) {
        this.inputs = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        inputs.add(ingredient);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return output == recipe.output &&
                quantity == recipe.quantity &&
                Objects.equals(inputs, recipe.inputs);
    }

}
