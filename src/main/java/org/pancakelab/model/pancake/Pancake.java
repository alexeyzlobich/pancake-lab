package org.pancakelab.model.pancake;

import java.util.List;
import java.util.Objects;

public final class Pancake {

    private final List<Ingredient> ingredients;
    private final String description;

    public Pancake(List<Ingredient> ingredients) {
        this.ingredients = List.copyOf(ingredients);
        this.description = buildDescription(ingredients);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pancake) obj;
        return Objects.equals(this.ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients);
    }

    private String buildDescription(List<Ingredient> ingredients) {
        List<String> ingredientNames = ingredients.stream()
                .map(Ingredient::getName)
                .toList();
        return "Delicious pancake with " + String.join(", ", ingredientNames) + "!";
    }
}
