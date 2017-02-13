package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class EntityCollidable implements Property {

    public static boolean describes(dObject entity) {
        return entity instanceof dEntity
                && ((dEntity) entity).isLivingEntity();
    }

    public static EntityCollidable getFrom(dObject entity) {
        if (!describes(entity)) {
            return null;
        }

        else {
            return new EntityCollidable((dEntity) entity);
        }
    }


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityCollidable(dEntity entity) {
        this.entity = entity;
    }

    dEntity entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        if (entity.getLivingEntity().isCollidable()) {
            return null;
        }
        return "false";
    }

    @Override
    public String getPropertyId() {
        return "collidable";
    }


    ///////////
    // dObject Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.collidable>
        // @returns Element(Boolean)
        // @mechanism dEntity.collidable
        // @group attributes
        // @description
        // Returns whether the entity can be collided with.
        // Must be a living entity.
        // -->
        if (attribute.startsWith("collidable")) {
            return new Element(entity.getLivingEntity().isCollidable()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name collidable
        // @input Element(Boolean)
        // @description
        // Sets whether the entity can be collided with.
        // Must be a living entity.
        // @tags
        // <e@entity.collidable>
        // -->
        if (mechanism.matches("collidable") && mechanism.requireBoolean()) {
            entity.getLivingEntity().setCollidable(mechanism.getValue().asBoolean());
        }
    }
}
