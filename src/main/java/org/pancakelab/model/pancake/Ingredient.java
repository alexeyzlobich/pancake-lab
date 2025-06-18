package org.pancakelab.model.pancake;

public enum Ingredient {

    DARK_CHOCOLATE("dark chocolate"),
    MILK_CHOCOLATE("milk chocolate"),
    MUSTARD("mustard"),
    WHIPPED_CREAM("whipped cream"),
    HAZELNUTS("hazelnuts");

    private final String name;

    Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
