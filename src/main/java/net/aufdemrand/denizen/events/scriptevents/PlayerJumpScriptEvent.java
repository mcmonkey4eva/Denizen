package net.aufdemrand.denizen.events.scriptevents;

import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class PlayerJumpScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player jumps
    //
    // @Triggers when a player jumps.
    //
    // @Context
    // <context.location> returns the location the player jumped at.
    //
    // -->

    public PlayerJumpScriptEvent() {
        instance = this;
    }
    public static PlayerJumpScriptEvent instance;
    public dLocation location;
    public PlayerMoveEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).equals("player jumps");

    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).equals("player jumps")
                // Did player move vertically?
                && (event.getTo().getBlockY() > event.getFrom().getBlockY()
                // and also that the player has a high velocity (jump instead of walking up stairs)
                && Math.abs(event.getPlayer().getVelocity().getY()) > 0.1
                // and that the player isn't in any form of fast moving vehicle
                && event.getPlayer().getVehicle() == null);    }

    @Override
    public String getName() {
        return "PlayerJumps";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PlayerMoveEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("location", location);
        return context;
    }

    @EventHandler
    public void onPlayerJumps(PlayerMoveEvent event) {
        location = new dLocation(event.getFrom());
        cancelled = event.isCancelled(); // Bukkit is broken, this doesn't work
        this.event = event;
        fire();
        event.setCancelled(cancelled); // Bukkit is broken, this doesn't work
    }
}
