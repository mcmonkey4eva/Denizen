package net.aufdemrand.denizen.objects.properties.traderecipe;

import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class TradeRecipeHasXp implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTradeRecipe;
    }

    public static TradeRecipeHasXp getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeRecipeHasXp((dTradeRecipe) recipe);
    }

    private dTradeRecipe recipe;

    public TradeRecipeHasXp(dTradeRecipe recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return String.valueOf(recipe.getRecipe().hasExperienceReward());
    }

    public String getPropertyId() {
        return "has_xp";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <traderecipe@recipe.has_xp>
        // @returns Element(Boolean)
        // @mechanism dTradeRecipe.has_xp
        // @description
        // Returns whether the trade recipe has an experience reward.
        // -->
        if (attribute.startsWith("has_xp")) {
            return new Element(recipe.getRecipe().hasExperienceReward()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTradeRecipe
        // @name has_xp
        // @input //
        // @description
        // //
        // @tags
        // //
        // -->
        if (mechanism.matches("has_xp") && mechanism.requireBoolean()) {
            recipe.getRecipe().setExperienceReward(mechanism.getValue().asBoolean());
        }
    }
}
