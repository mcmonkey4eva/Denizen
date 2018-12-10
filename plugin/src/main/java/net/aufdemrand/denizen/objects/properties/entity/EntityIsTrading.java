package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.inventory.Merchant;

public class EntityIsTrading implements Property {

    public static boolean describes(dObject entity) {
        return entity instanceof dEntity &&
                ((dEntity) entity).getBukkitEntity() instanceof Merchant;
    }

    public static EntityIsTrading getFrom(dObject entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityIsTrading((dEntity) entity);
    }

    private dEntity entity;

    public EntityIsTrading(dEntity entity) {
        this.entity = entity;
    }

    public String getPropertyString() {
        if (entity.getBukkitEntity() == null) {
            return null;
        }
        return String.valueOf(((Merchant) entity.getBukkitEntity()).isTrading());
    }

    public String getPropertyId() {
        return "is_trading";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.is_trading>
        // @returns Element(Boolean)
        // @description
        // Returns a list of the Villager's trade recipes.
        // -->
        if (attribute.startsWith("is_trading")) {
            return new Element(((Merchant) entity.getBukkitEntity()).isTrading())
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

    }
}
