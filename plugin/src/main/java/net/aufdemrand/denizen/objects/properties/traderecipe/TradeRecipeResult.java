package net.aufdemrand.denizen.objects.properties.traderecipe;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class TradeRecipeResult implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTradeRecipe;
    }

    public static TradeRecipeResult getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeRecipeResult((dTradeRecipe) recipe);
    }

    private dTradeRecipe recipe;

    public TradeRecipeResult(dTradeRecipe recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return (new dItem(recipe.getRecipe().getResult())).identify();
    }

    public String getPropertyId() {
        return "result";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <traderecipe@recipe.result>
        // @returns dItem
        // @mechanism dTradeRecipe.result
        // @description
        // Returns the result of the trade recipe.
        // -->
        if (attribute.startsWith("result")) {
            return new dItem(recipe.getRecipe().getResult()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTradeRecipe
        // @name result
        // @input dItem
        // @description
        // Sets the trade recipe's result.
        // @tags
        // <traderecipe@recipe.result>
        // -->
        if (mechanism.matches("result") && mechanism.requireObject(dItem.class)) {
            ItemStack item;
            item = mechanism.hasValue() ? mechanism.getValue().asType(dItem.class).getItemStack() : new ItemStack(Material.AIR);

            MerchantRecipe oldRecipe = recipe.getRecipe();
            MerchantRecipe newRecipe = new MerchantRecipe(item, oldRecipe.getUses(), oldRecipe.getMaxUses(), oldRecipe.hasExperienceReward());
            newRecipe.setIngredients(oldRecipe.getIngredients());

            recipe.setRecipe(newRecipe);
        }
    }
}
