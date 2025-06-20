package org.pancakelab.model.pancake;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PancakeTest {

    @Test
    void ShouldCreatePancake_WhenThereAreIngredients() {
        // given
        List<Ingredient> ingredients = List.of(
                Ingredient.DARK_CHOCOLATE,
                Ingredient.MILK_CHOCOLATE
        );

        // when
        Pancake pancake = new Pancake(ingredients);

        // then
        assertThat(pancake.getDescription()).isEqualTo("Delicious pancake with dark chocolate, milk chocolate!");
    }

    @Test
    void ShouldCreatePancake_WhenNoIngredients() {
        // given
        List<Ingredient> ingredients = List.of();

        // when
        Pancake pancake = new Pancake(ingredients);

        // then
        assertThat(pancake.getDescription()).isEqualTo("Just plain delicious pancake!");
    }

    @Test
    void ShouldPancakeEquals_WhenSameIngredientsInSameOrder() {
        // given
        Pancake pancake1 = new Pancake(List.of(Ingredient.DARK_CHOCOLATE, Ingredient.MILK_CHOCOLATE));
        Pancake pancake2 = new Pancake(List.of(Ingredient.DARK_CHOCOLATE, Ingredient.MILK_CHOCOLATE));

        // when/then
        assertThat(pancake1).isEqualTo(pancake2);
    }

    @Test
    void ShouldPancakeEquals_WhenSameIngredientsInDifferentOrder() {
        // given
        Pancake pancake1 = new Pancake(List.of(Ingredient.DARK_CHOCOLATE, Ingredient.MILK_CHOCOLATE));
        Pancake pancake2 = new Pancake(List.of(Ingredient.MILK_CHOCOLATE, Ingredient.DARK_CHOCOLATE));

        // when/then
        assertThat(pancake1).isEqualTo(pancake2);
    }

    @Test
    void ShouldPancakeEquals_WhenNotSameIngredients() {
        // given
        Pancake pancake1 = new Pancake(List.of(Ingredient.DARK_CHOCOLATE, Ingredient.MILK_CHOCOLATE));
        Pancake pancake2 = new Pancake(List.of(Ingredient.DARK_CHOCOLATE));

        // when/then
        assertThat(pancake1).isNotEqualTo(pancake2);
    }
}