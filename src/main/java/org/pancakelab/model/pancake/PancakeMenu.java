package org.pancakelab.model.pancake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum PancakeMenu {
    INSTANCE;

    private final Map<String, Pancake> menuEntries = new HashMap<>();

    PancakeMenu() {
        // initial pancake recipes
        doAddPancakeToMenu(new Pancake(List.of(Ingredient.DARK_CHOCOLATE)));
        doAddPancakeToMenu(new Pancake(List.of(
                Ingredient.DARK_CHOCOLATE,
                Ingredient.WHIPPED_CREAM,
                Ingredient.HAZELNUTS
        )));
        doAddPancakeToMenu(new Pancake(List.of(
                Ingredient.DARK_CHOCOLATE,
                Ingredient.WHIPPED_CREAM
        )));
        doAddPancakeToMenu(new Pancake(List.of(
                Ingredient.MILK_CHOCOLATE,
                Ingredient.HAZELNUTS
        )));
        doAddPancakeToMenu(new Pancake(List.of(Ingredient.MILK_CHOCOLATE)));
    }

    public Optional<Pancake> findPancake(String pancakeDescription) {
        return Optional.ofNullable(menuEntries.get(pancakeDescription));
    }

    private void doAddPancakeToMenu(Pancake pancake) {
        menuEntries.put(pancake.getDescription(), pancake);
    }
}
