package org.pancakelab.model.pancake;

import java.util.List;
import java.util.Objects;

public final class Pancake {

    private final List<Ingredient> ingredients;
    private final String description;

    /**
     * Creates a new Pancake with the specified ingredients.
     *
     * @param ingredients the list of ingredients for the pancake
     * @throws IllegalArgumentException if the ingredients list is null
     */
    // TODO: if RAM is an issue, implement flyweight pattern for pancakes to manage custom pancakes efficiently
    public Pancake(List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new IllegalArgumentException("Ingredients cannot be null");
        }
        this.ingredients = ingredients.stream()
                .sorted()
                .toList();
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
        if (ingredients.isEmpty()) {
            return "Just plain delicious pancake!";
        } else {
            List<String> ingredientNames = ingredients.stream()
                    .map(Ingredient::getName)
                    .toList();
            return "Delicious pancake with " + String.join(", ", ingredientNames) + "!";
        }
    }
}
