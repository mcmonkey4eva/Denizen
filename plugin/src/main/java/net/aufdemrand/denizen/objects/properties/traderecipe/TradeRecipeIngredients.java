package net.aufdemrand.denizen.objects.properties.traderecipe;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeRecipeIngredients implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTradeRecipe;
    }

    public static TradeRecipeIngredients getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeRecipeIngredients((dTradeRecipe) recipe);
    }

    private dTradeRecipe recipe;

    public TradeRecipeIngredients(dTradeRecipe recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        dList ingredients = new dList();
        for (ItemStack item : recipe.getRecipe().getIngredients()) {
            ingredients.addObject(new dItem(item));
        }
        return ingredients.identify();
    }

    public String getPropertyId() {
        return "ingredients";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <traderecipe@recipe.ingredients>
        // @returns dList(dItem)
        // @mechanism dTradeRecipe.ingredients
        // @description
        // Returns the ingredients of the trade recipe.
        // -->
        if (attribute.startsWith("ingredients")) {
            ArrayList<dItem> itemList = new ArrayList<>();
            for (ItemStack item : recipe.getRecipe().getIngredients()) {
                itemList.add(new dItem(item));
            }
            return new dList(itemList).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTradeRecipe
        // @name ingredients
        // @input dList(dItem)
        // @description
        // Sets the trade recipe's ingredients. Provide no input or an empty list for no ingredients.
        // @tags
        // <traderecipe@recipe.ingredients>
        // -->
        if (mechanism.matches("ingredients")) {
            List<ItemStack> ingredients = new ArrayList<>();
            dList list = mechanism.getValue().asType(dList.class);

            if (!mechanism.hasValue() || mechanism.getValue().asType(dList.class).isEmpty()) {
                recipe.getRecipe().setIngredients(ingredients);
                return;
            }
            if (list.size() > 2) {
                // Maybe come up with a slightly better error message?
                dB.echoError("Trade recipe given " + list.size() + " ingredients. There must be 1 or 2!");
            }

            for (int i = 0; i < Math.max(list.size(), 2); i++) {
                ingredients.add(dItem.valueOf(list.get(i)).getItemStack());
            }
            recipe.getRecipe().setIngredients(ingredients);
        }
    }
}
