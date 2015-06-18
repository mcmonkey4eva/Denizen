package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EntityTamesScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity tamed
    // <entity> tamed
    // player tames entity
    // player tames <entity>
    //
    // @Cancellable true
    //
    // @Triggers when an entity is tamed.
    //
    // @Context
    // <context.entity> returns a dEntity of the tamed entity.
    //
    // -->

    public EntityTamesScriptEvent() {
        instance = this;
    }
    public static EntityTamesScriptEvent instance;
    public dEntity entity;
    private String cmd;
    public EntityTameEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        cmd = CoreUtilities.getXthArg(1, lower);
        String entOne = CoreUtilities.getXthArg(0, lower);
        List<String> types = Arrays.asList("entity", "player", "npc");
        return (types.contains(entOne) || dEntity.matches(entOne))
                && (cmd.equals("tames") || cmd.equals("tamed"));
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        cmd = CoreUtilities.getXthArg(1, lower);
        String entOne = CoreUtilities.getXthArg(0, lower);
        String entTwo = CoreUtilities.getXthArg(2, lower);
        List<String> types = Arrays.asList("entity", "player", "npc");

        if (cmd.equals("tamed") && (!types.contains(entOne) || !entity.matchesEntity(entOne))) {
            return false;
        }
        if (cmd.equals("tames")) {
            if (!types.contains(entTwo) || !entity.matchesEntity(entTwo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "EntityTames";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        EntityTameEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        dPlayer player = null;
        dNPC npc = null;
        // TODO This needs to be fixed, owner is mostly to be a player, not the entity itself.
        if (entity.isCitizensNPC()) {
            npc = entity.getDenizenNPC();
        }
        else if (entity.isPlayer()) {
            player = entity.getDenizenPlayer();
        }

        return new BukkitScriptEntryData(player, npc);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("entity", entity);
        return context;
    }

    @EventHandler
    public void onEntityTames(EntityTameEvent event) {
        entity = new dEntity(event.getEntity());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
