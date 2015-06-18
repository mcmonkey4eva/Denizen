package net.aufdemrand.denizen.events.world;

import net.aufdemrand.denizen.objects.dInventory;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

import java.util.HashMap;

public class BrewsScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // brewing stand brews
    //
    // @Cancellable true
    //
    // @Triggers when a brewing stand brews a potion.
    //
    // @Context
    // <context.location> returns the dLocation of the brewing stand.
    // <context.inventory> returns the dInventory of the brewing stand's contents.
    //
    // -->

    public BrewsScriptEvent() {
        instance = this;
    }
    public static BrewsScriptEvent instance;
    public dInventory inventory;
    public dLocation location;
    public BrewEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).equals("brewing stand brews");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        return true;
    }

    @Override
    public String getName() {
        return "Brews";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        BrewEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("location", location);
        context.put("inventory", inventory);
        return context;
    }

    @EventHandler
    public void onBrews(BrewEvent event) {
        location = new dLocation(event.getBlock().getLocation());
        inventory = dInventory.mirrorBukkitInventory(event.getContents());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
