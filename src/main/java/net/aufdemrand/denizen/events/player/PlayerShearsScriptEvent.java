package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.HashMap;

public class PlayerShearsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player shears entity (in <area>)
    // player shears <entity> (in <area>)
    // player shears <color> sheep (in <area>)
    //
    // @Cancellable true
    //
    // @Triggers when a player shears an entity.
    //
    // @Context
    // <context.entity> returns the dEntity of the sheep.
    //
    // -->

    public PlayerShearsScriptEvent() {
        instance = this;
    }

    public static PlayerShearsScriptEvent instance;
    public dEntity entity;
    public PlayerShearEntityEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player shears");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String ent = CoreUtilities.getXthArg(3, lower).equals("sheep") ? "sheep" : CoreUtilities.getXthArg(2, lower);
        if (!ent.equals("sheep") && !entity.matchesEntity(ent)) {
            return false;
        }

        String color = CoreUtilities.getXthArg(3, lower).equals("sheep") ? CoreUtilities.getXthArg(2, lower) : "";
        if(color.length() > 0 && !color.equals(CoreUtilities.toLowerCase(((Sheep) entity.getBukkitEntity()).getColor().name()))) {
            return false;
        }

        return runInCheck(scriptContainer, s, lower, entity.getLocation());
    }

    @Override
    public String getName() {
        return "PlayerShears";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PlayerShearEntityEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(dEntity.isPlayer(event.getPlayer()) ? dEntity.getPlayerFrom(event.getPlayer()) : null, null);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("entity", entity);
        context.put("state", entity); //DEPRECATED because...dumb
        return context;
    }

    @EventHandler
    public void onPlayerShears(PlayerShearEntityEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        entity = new dEntity(event.getEntity());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
