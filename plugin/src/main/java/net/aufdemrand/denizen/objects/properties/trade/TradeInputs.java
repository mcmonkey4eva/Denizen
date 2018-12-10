package net.aufdemrand.denizen.objects.properties.trade;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dTrade;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeInputs implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTrade;
    }

    public static TradeInputs getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeInputs((dTrade) recipe);
    }

    private dTrade recipe;

    public TradeInputs(dTrade recipe) {
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
        return "inputs";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <trade@trade.inputs>
        // @returns dList(dItem)
        // @mechanism dTrade.inputs
        // @description
        // Returns the list of items required to make the trade.
        // -->
        if (attribute.startsWith("inputs")) {
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
        // @object dTrade
        // @name inputs
        // @input dList(dItem)
        // @description
        // Sets the items required to make a successful trade. Use an empty input to make the trade impossible.
        // @tags
        // <trade@trade.input>
        // -->
        if (mechanism.matches("inputs")) {
            List<ItemStack> ingredients = new ArrayList<>();
            dList list = mechanism.getValue().asType(dList.class);

            if (!mechanism.hasValue() || mechanism.getValue().asType(dList.class).isEmpty()) {
                recipe.getRecipe().setIngredients(ingredients);
                return;
            }
            if (list.size() > 2) {
                // Maybe come up with a slightly better error message?
                dB.echoError("Trade recipe given " + list.size() + " inputs. There must be 1 or 2!");
            }

            for (int i = 0; i < Math.max(list.size(), 2); i++) {
                ingredients.add(dItem.valueOf(list.get(i)).getItemStack());
            }
            recipe.getRecipe().setIngredients(ingredients);
        }
    }
}