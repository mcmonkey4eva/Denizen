package net.aufdemrand.denizen.objects.properties.trade;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dTrade;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class TradeOutput implements Property {

    public static boolean describes(dObject recipe) {
        return recipe instanceof dTrade;
    }

    public static TradeOutput getFrom(dObject recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeOutput((dTrade) recipe);
    }

    private dTrade recipe;

    public TradeOutput(dTrade recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return (new dItem(recipe.getRecipe().getResult())).identify();
    }

    public String getPropertyId() {
        return "output";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <trade@trade.output>
        // @returns dItem
        // @mechanism dTradeRecipe.output
        // @description
        // Returns what the trade will give the player.
        // -->
        if (attribute.startsWith("output")) {
            return new dItem(recipe.getRecipe().getResult()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTrade
        // @name output
        // @input dItem
        // @description
        // Sets what the trade will give the player.
        // @tags
        // <trade@trade.output>
        // -->
        if (mechanism.matches("output") && mechanism.requireObject(dItem.class)) {
            ItemStack item;
            item = mechanism.hasValue() ? mechanism.getValue().asType(dItem.class).getItemStack() : new ItemStack(Material.AIR);

            MerchantRecipe oldRecipe = recipe.getRecipe();
            MerchantRecipe newRecipe = new MerchantRecipe(item, oldRecipe.getUses(), oldRecipe.getMaxUses(), oldRecipe.hasExperienceReward());
            newRecipe.setIngredients(oldRecipe.getIngredients());

            recipe.setRecipe(newRecipe);
        }
    }
}
