package net.aufdemrand.denizen.objects.properties.traderecipe;

import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class TradeRecipeUses implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTradeRecipe;
    }

    public static TradeRecipeUses getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeRecipeUses((dTradeRecipe) recipe);
    }

    private dTradeRecipe recipe;

    public TradeRecipeUses(dTradeRecipe recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return String.valueOf(recipe.getRecipe().getUses());
    }

    public String getPropertyId() {
        return "uses";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <traderecipe@recipe.uses>
        // @returns Element(Number)
        // @mechanism dTradeRecipe.uses
        // @description
        // Returns how many times the trade recipe has been used.
        // -->
        if (attribute.startsWith("uses")) {
            return new Element(recipe.getRecipe().getUses()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTradeRecipe
        // @name uses
        // @input Element(Number)
        // @description
        // Sets the amount of times the trade recipe has been used.
        // @tags
        // <traderecipe@recipe.uses>
        // -->
        if (mechanism.matches("uses") && mechanism.requireInteger()) {
            recipe.getRecipe().setUses(mechanism.getValue().asInt());
        }
    }
}
