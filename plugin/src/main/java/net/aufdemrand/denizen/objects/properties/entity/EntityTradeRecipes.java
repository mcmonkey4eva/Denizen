package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dTradeRecipe;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;

public class EntityTradeRecipes implements Property {

    public static boolean describes(dObject entity) {
        return entity instanceof dEntity &&
                ((dEntity) entity).getBukkitEntity() instanceof Merchant;
    }

    public static EntityTradeRecipes getFrom(dObject entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityTradeRecipes((dEntity) entity);
    }

    private dEntity entity;

    public EntityTradeRecipes(dEntity entity) {
        this.entity = entity;
    }

    public String getPropertyString() {
        if (((Merchant) entity.getBukkitEntity()).getRecipes() == null) {
            return null;
        }
        return entity.getTradeRecipes().identify();
    }

    public String getPropertyId() {
        return "trade_recipes";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.trade_recipes>
        // @returns dList(dTradeRecipe)
        // @mechanism dEntity.trade_recipes
        // @description
        // Returns a list of the Villager's trade recipes.
        // -->
        if (attribute.startsWith("trade_recipes")) {
            return entity.getTradeRecipes().getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name trade_recipes
        // @input dList(dTradeRecipe)
        // @description
        // Sets the trade recipes that the entity will offer.
        // @tags
        // <e@entity.trade_recipes>
        // -->
        if (mechanism.matches("trade_recipes")) {
            ArrayList<MerchantRecipe> recipes = new ArrayList<>();
            for (dTradeRecipe recipe : mechanism.getValue().asType(dList.class).filter(dTradeRecipe.class)) {
                recipes.add(recipe.getRecipe());
            }
            ((Merchant) entity.getBukkitEntity()).setRecipes(recipes);
        }
    }
}
