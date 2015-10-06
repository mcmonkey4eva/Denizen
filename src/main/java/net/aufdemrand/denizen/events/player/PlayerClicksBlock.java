package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dMaterial;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerClicksBlock extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player clicks block (in <area>)
    // player (<click type>) clicks (<material>) (at:<location>) (with <item>) (in <area>)
    // player (<click type>) clicks block (at:<location>) (with <item>) (in <area>)
    // player stands on <pressure plate> (in <area>)
    //
    // @Regex //TODO
    //
    // @Cancellable true
    //
    // @Triggers when a player clicks on a block or stands on a pressure plate.
    //
    // @Context
    // <context.item> returns the dItem the player is clicking with.
    // <context.location> returns the dLocation the player is clicking on.
    // <context.click_type> returns an Element of the click type.
    // <context.relative> returns a dLocation of the air block in front of the clicked block.
    //
    // @Determine
    // dItem to change the item being consumed.
    //
    // -->

    public PlayerClicksBlock() {
        instance = this;
    }

    public static PlayerClicksBlock instance;

    public dItem item;
    public dLocation location;
    public Element click_type;
    public dLocation relative;
    private Action action;
    private dMaterial blockMaterial;
    public PlayerInteractEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String arg1 = CoreUtilities.getXthArg(1, lower);
        String arg2 = CoreUtilities.getXthArg(2, lower);
        return lower.startsWith("player")
                && (arg1.equals("clicks") || arg2.equals("clicks") || arg1.equals("stands"));
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String arg1 = CoreUtilities.getXthArg(1, lower);
        String arg2 = CoreUtilities.getXthArg(2, lower);
        String arg3 = CoreUtilities.getXthArg(3, lower);
        Boolean click = false;

        if (arg1.equals("left") || arg1.equals("right")) {
            click = true;
            if (arg1.equals("right") && (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR))) {
                return false;
            }
            if (arg1.equals("left") && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))) {
                return false;
            }
        }

        // Check for standing command
        String cmd = (click ? arg2 : arg1);
        if ((cmd.equals("stands") && !action.equals(Action.PHYSICAL)) || (cmd.equals("clicks") && action.equals(Action.PHYSICAL))) {
            return false;
        }

        // Check for block material for click/standing on
        String blk = (click || cmd.equals("stands") ? arg3 : arg2);
        if (!tryMaterial(blockMaterial, blk)) {
            return false;
        }

        // Check for older "with <item>" format
        int index;
        List<String> data = CoreUtilities.split(lower, ' ');
        for (index = 0; index < data.size(); index++) {
            if (data.get(index).equals("with")) {
                break;
            }
        }

        if (index < data.size()) {
            // Check with object
            if (!tryItem(item, CoreUtilities.getXthArg(index + 1, lower))) {
                return false;
            }
        }

        // Check for "at:location"
        if (!runAtCheck(scriptContainer, s, lower, location)) {
            return false;
        }
        // Check for "in <area>"
        return runInCheck(scriptContainer, s, lower, location);
    }

    @Override
    public String getName() {
        return "PlayerClicksBlock";
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
        if (name.equals("item")) {
            return item;
        }
        if (name.equals("click_type")) {
            return click_type;
        }
        if (name.equals("location")) {
            return location;
        }
        if (name.equals("relative")) {
            return relative;
        }
        return super.getContext(name);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerClicksBlock(PlayerInteractEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        Block block = event.getClickedBlock();
        blockMaterial = dMaterial.getMaterialFrom(block.getType(), block.getData());
        location = new dLocation(block.getLocation());
        action = event.getAction();
        relative = null;
        if (event.getBlockFace() != null && event.getClickedBlock() != null) {
            relative = new dLocation(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation());
        }
        item = null;
        if (event.hasItem()) {
            item = new dItem(event.getItem());
        }
        click_type = new Element(action.name());
        item = new dItem(event.getItem());
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
