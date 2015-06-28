package net.aufdemrand.denizen.events.world;

import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;

public class RedstoneScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // redstone recalculated
    //
    // @Warning This event fires very very rapidly!
    //
    // @Triggers when a redstone wire is recalculated.
    // @Context
    // <context.location> returns the location of the block.
    // <context.old_current> returns what the redstone power level was.
    // <context.new_current> returns what the redstone power level is becoming.
    //
    // @Determine
    // Element (Number) set the current value to a specific value.
    //
    // -->

    public RedstoneScriptEvent() {
        instance = this;
    }

    public static RedstoneScriptEvent instance;

    public dLocation location;
    public Element old_current;
    public Element new_current;
    public BlockRedstoneEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("redstone recalculated");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        return true;
    }

    @Override
    public String getName() {
        return "RedstoneRecalculated";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        BlockRedstoneEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        Element power = new Element(determination);
        if (power.isInt()) {
            new_current = power;
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("location", location);
        context.put("old_current", old_current);
        context.put("new_current", new_current);
        return context;
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        location = new dLocation(event.getBlock().getLocation());
        old_current = new Element(event.getOldCurrent());
        new_current = new Element(event.getNewCurrent());
        this.event = event;
        fire();
        event.setNewCurrent(new_current.asInt());
    }
}
