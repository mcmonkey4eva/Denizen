package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dMaterial;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerStandsOn extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player stands on <pressure plate> (in <area>)
    //
    // @Regex ^on player stands on [^\s]+( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Cancellable true
    //
    // @Triggers when a player stands on a pressure plate.
    //
    // @Context
    // <context.location> returns the dLocation the player is standing on.
    //
    // -->

    public PlayerStandsOn() {
        instance = this;
    }

    public static PlayerStandsOn instance;

    public dItem item;
    public dLocation location;
    private dMaterial blockMaterial;
    public PlayerInteractEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player stands on");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String plate = CoreUtilities.getXthArg(3, lower);

        // Check for block material for standing on
        if (!tryMaterial(blockMaterial, plate)) {
            return false;
        }

        // Check for "in <area>"
        return runInCheck(scriptContainer, s, lower, location);
    }

    @Override
    public String getName() {
        return "PlayerStandsOn";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event != null ? dEntity.getPlayerFrom(event.getPlayer()) : null, null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        return super.getContext(name);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerStandsOn(PlayerInteractEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        Block block = event.getClickedBlock();
        blockMaterial = dMaterial.getMaterialFrom(block.getType(), block.getData());
        location = new dLocation(block.getLocation());
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
