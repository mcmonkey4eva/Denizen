package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.utilities.Conversion;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.entity.Position;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EntityShootsScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity shoots bow
    // <entity> shoots bow
    // entity shoots <item>
    // <entity> shoots <item>
    //
    // @Cancellable true
    //
    // @Triggers when an entity shoots something out of a bow.
    //
    // @Context
    // <context.entity> returns the dEntity that shot the bow.
    // <context.projectile> returns a dEntity of the projectile.
    // <context.bow> returns the bow item used to shoot.
    // <context.force> returns the force of the shot.
    //
    // @Determine
    // dList(dEntity) to change the projectile(s) being shot.
    //
    // -->

    public EntityShootsScriptEvent() {
        instance = this;
    }
    public static EntityShootsScriptEvent instance;
    public dEntity entity;
    public dEntity projectile;
    public dItem bow;
    public Float force;
    public EntityShootBowEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(1, lower);
        String entOne = CoreUtilities.getXthArg(0, lower);
        List<String> types = Arrays.asList("entity", "player", "npc");
        return (types.contains(entOne) || dEntity.matches(entOne))
                && cmd.equals("shoots");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String target = CoreUtilities.getXthArg(0,lower);
        List<String> types = Arrays.asList("entity", "player", "npc");
        if (!types.contains(target) && !entity.matchesEntity(target)) {
            return false;
        }
        String device = CoreUtilities.getXthArg(2,lower);
        if (target.length() > 0) {
            if (!dItem.matches(device)
                    && (!device.equals(bow.identifyNoIdentifier()) && !device.equals(bow.identifySimple()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "EntityShoots";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        EntityShootBowEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (aH.Argument.valueOf(determination).matchesArgumentList(dEntity.class)) {
            cancelled = true;
            // Get the list of entities
            Object list = dList.valueOf(determination).filter(dEntity.class);

            @SuppressWarnings("unchecked")
            List<dEntity> newProjectiles = (List<dEntity>) list;

            // Go through all the entities, spawning/teleporting them
            for (dEntity newProjectile : newProjectiles) {
                newProjectile.spawnAt(entity.getEyeLocation()
                        .add(entity.getEyeLocation().getDirection()));

                // Set the entity as the shooter of the projectile,
                // where applicable
                if (newProjectile.isProjectile()) {
                    newProjectile.setShooter(entity);
                }
            }

            // Mount the projectiles on top of each other
            Position.mount(Conversion.convertEntities(newProjectiles));

            // Get the last entity on the list, i.e. the one at the bottom
            // if there are many mounted on top of each other
            Entity lastProjectile = newProjectiles.get
                    (newProjectiles.size() - 1).getBukkitEntity();

            // Give it the same velocity as the arrow that would
            // have been shot by the bow
            // Note: No, I can't explain why this has to be multiplied by three, it just does.
            lastProjectile.setVelocity(event.getEntity().getLocation()
                    .getDirection().multiply(event.getForce() * 3));
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(entity.isPlayer() ? dEntity.getPlayerFrom(event.getEntity()): null,
                entity.isCitizensNPC() ? dEntity.getNPCFrom(event.getEntity()): null);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("entity", entity);
        context.put("projectile", projectile);
        context.put("bow", bow);
        context.put("force", new Element(force));
        return context;
    }

    @EventHandler
    public void onEntityShoots(EntityShootBowEvent event) {
        entity = new dEntity(event.getEntity());
        projectile = new dEntity(event.getProjectile());
        bow = new dItem(event.getBow());
        force = event.getForce();
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
