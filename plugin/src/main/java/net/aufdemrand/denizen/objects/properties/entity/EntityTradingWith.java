package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.inventory.Merchant;

public class EntityTradingWith implements Property {

    public static boolean describes(dObject entity) {
        return entity instanceof dEntity &&
                ((dEntity) entity).getBukkitEntity() instanceof Merchant;
    }

    public static EntityTradingWith getFrom(dObject entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityTradingWith((dEntity) entity);
    }

    private dEntity entity;

    public EntityTradingWith(dEntity entity) {
        this.entity = entity;
    }

    public String getPropertyString() {
        if (((Merchant) entity.getBukkitEntity()).getRecipes() == null) {
            return null;
        }
        return entity.getTradeRecipes().identify();
    }

    public String getPropertyId() {
        return "trading_with";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.trading_with>
        // @returns dEntity
        // @description
        // Returns the player the Villager is trading with, or itself if it is not trading.
        // -->
        if (attribute.startsWith("trading_with")) {
            return new dEntity(((Merchant) entity.getBukkitEntity()).getTrader()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

    }
}
