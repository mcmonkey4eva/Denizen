package net.aufdemrand.denizen.events.scriptevents;

import net.aufdemrand.denizen.objects.dCuboid;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;

import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;

public class EntitySpawnScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity spawns
    // entity spawns (in <notable cuboid>) (because <cause>)
    // <entity> spawns
    // <entity> spawns (in <notable cuboid>) (because <cause>)
    //
    // @Regex on (\w+) spawns(?: in (\w+))?(?: because (\w+))?
    //
    // @Warning This event may fire very rapidly.
    //
    // @Cancellable true
    //
    // @Triggers when an entity spawns.
    //
    // @Context
    // <context.entity> returns the dEntity that spawned.
    // <context.location> returns the location the entity will spawn at.
    // <context.cuboids> returns a list of cuboids that the entity spawned inside.
    // <context.reason> returns the reason the entity spawned.
    // Reasons: <@link url https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html>
    //
    // -->

    public EntitySpawnScriptEvent() {
        instance = this;
    }
    public static EntitySpawnScriptEvent instance;
    public dEntity entity;
    public dLocation location;
    public dList cuboids;
    public Element reason;
    public CreatureSpawnEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.contains("spawns");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);

        if (lower.equals("entity spawns")) return true;

        Boolean entityvalid = true;
        if (!lower.startsWith("entity") &&(!lower.startsWith(entity.identifyType()) || !lower.startsWith(entity.identifySimple()))) {
            entityvalid = false;
        }

        cuboids = new dList();
        for (dCuboid cuboid: dCuboid.getNotableCuboidsContaining(event.getLocation())) {
            cuboids.add(cuboid.identify());
        }

        Boolean cuboidvalid = true;
        if (lower.contains(" in "))
            if (!cuboids.contains(lower.substring(lower.lastIndexOf("in ")+ 3, lower.lastIndexOf(" because",-1))))
                cuboidvalid = false;

        Boolean reasonvalid = true;
        if (lower.contains("because"))
            if (!lower.substring(lower.lastIndexOf("because") + 8).equals(reason.asString()))
                reasonvalid = false;

        return reasonvalid && cuboidvalid && entityvalid;
    }

    @Override
    public String getName() {
        return "EntitySpawns";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("entity", entity);
        context.put("location", location);
        context.put("cuboids", cuboids);
        context.put("reason", reason);
        return context;
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        entity = new dEntity(event.getEntity());
        location = new dLocation(event.getLocation());
        // Find cuboids and build list
        cuboids = new dList();
        for (dCuboid cuboid: dCuboid.getNotableCuboidsContaining(event.getLocation())) {
            cuboids.add(cuboid.identify());
        }
        reason = new Element(event.getSpawnReason().name());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
