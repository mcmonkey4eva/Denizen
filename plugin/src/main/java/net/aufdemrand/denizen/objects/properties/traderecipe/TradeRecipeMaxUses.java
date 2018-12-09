package net.aufdemrand.denizen.objects.properties.traderecipe;

import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class TradeRecipeMaxUses implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTradeRecipe;
    }

    public static TradeRecipeMaxUses getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeRecipeMaxUses((dTradeRecipe) recipe);
    }

    private dTradeRecipe recipe;

    public TradeRecipeMaxUses(dTradeRecipe recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return String.valueOf(recipe.getRecipe().getMaxUses());
    }

    public String getPropertyId() {
        return "max_uses";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <traderecipe@recipe.max_uses>
        // @returns Element(Boolean)
        // @mechanism dTradeRecipe.max_uses
        // @description
        // Returns the maximum amount of times that the trade recipe can be used.
        // -->
        if (attribute.startsWith("max_uses")) {
            return new Element(recipe.getRecipe().getMaxUses()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTradeRecipe
        // @name //
        // @input //
        // @description
        // //
        // @tags
        // //
        // -->
        if (mechanism.matches("max_uses") && mechanism.requireInteger()) {
            recipe.getRecipe().setMaxUses(mechanism.getValue().asInt());
        }
    }
}
