package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.*;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlacesBlockScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player places block (in <area>)
    // player places <material> (in <area>)
    //
    // @Regex ^on player places [^\s]+( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Cancellable true
    //
    // @Triggers when a player places a block.
    //
    // @Context
    // <context.location> returns the dLocation of the block that was placed.
    // <context.old_material> returns the dMaterial of the original block (i.e. AIR, WATER, LAVA).
    // <context.new_material> returns the dMaterial of the block that was placed.
    // <context.cuboids> DEPRECATED.
    // <context.item_in_hand> returns the dItem of the item in hand.
    //
    // -->

    public PlayerPlacesBlockScriptEvent() {
        instance = this;
    }

    public static PlayerPlacesBlockScriptEvent instance;
    public dLocation location;
    public dMaterial new_material;
    public dMaterial old_material;
    public dList cuboids;
    public dItem item_in_hand;
    public BlockPlaceEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String mat = CoreUtilities.getXthArg(2, lower);
        return lower.startsWith("player places")
                && (!mat.equals("hanging") && !mat.equals("painting") && !mat.equals("item_frame") && !mat.equals("leash_hitch"));
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);

        String mat = CoreUtilities.getXthArg(2, lower);
        if (!tryItem(item_in_hand, mat) && !tryMaterial(new_material, mat)) {
            return false;
        }

        if (!runInCheck(scriptContainer, s, lower, location)) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "PlayerPlacesBlock";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        BlockPlaceEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new dPlayer(event.getPlayer()), null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("material") || (name.equals("new_material"))) {
            return new_material;
        }
        else if (name.equals("old_material")) {
            return old_material;
        }
        else if (name.equals("item_in_hand")) {
            return item_in_hand;
        }
        else if (name.equals("cuboids")) { // NOTE: Deprecated in favor of context.location.cuboids
            return cuboids;
        }
        return super.getContext(name);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlacesBlock(BlockPlaceEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        new_material = dMaterial.getMaterialFrom(event.getBlock().getType(), event.getBlock().getData());
        old_material = dMaterial.getMaterialFrom(event.getBlockReplacedState().getType(), event.getBlockReplacedState().getBlock().getData());
        location = new dLocation(event.getBlock().getLocation());
        cuboids = new dList();
        for (dCuboid cuboid : dCuboid.getNotableCuboidsContaining(location)) {
            cuboids.add(cuboid.identifySimple());
        }
        cancelled = event.isCancelled();
        item_in_hand = new dItem(event.getItemInHand());
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }

}
