package org.pancakelab.model.pancake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PancakeMenu {

    private final Map<String, Pancake> menuEntries = new HashMap<>();

    public PancakeMenu() {
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

    /**
     * Finds a pancake in the menu by its description.
     *
     * @param pancakeDescription the description of the pancake to find
     * @return an Optional containing the pancake if found, or an empty Optional if no pancake
     * with the given description exists in the menu
     */
    public Optional<Pancake> findPancakeByDescription(String pancakeDescription) {
        return Optional.ofNullable(menuEntries.get(pancakeDescription));
    }

    private void doAddPancakeToMenu(Pancake pancake) {
        menuEntries.put(pancake.getDescription(), pancake);
    }
}
