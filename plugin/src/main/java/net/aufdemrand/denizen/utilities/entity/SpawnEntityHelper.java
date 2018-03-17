package net.aufdemrand.denizen.utilities.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Consumer;

import java.util.ArrayList;

/**
 * Applies mechanisms to an entity before spawning it
 */
public class SpawnEntityHelper {

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Entity spawn(Location location, EntityType bukkitEntityType, final ArrayList<Mechanism> mechanisms) {
        Consumer<? extends Entity> consumer = new Consumer<Entity>() {
            @Override
            public void accept(Entity bukkitEntity) {
                dEntity entity = new dEntity(bukkitEntity);
                for (Mechanism mechanism : new ArrayList<Mechanism>(mechanisms)) {
                    if (dEntity.earlyValidMechanisms.contains(CoreUtilities.toLowerCase(mechanism.getName()))) {
                        entity.adjust(mechanism);
                        mechanisms.remove(mechanism);
                    }
                }
            }
        };
        return location.getWorld().spawn(location, (Class<T>) bukkitEntityType.getEntityClass(), (Consumer<T>) consumer);
    }
}
