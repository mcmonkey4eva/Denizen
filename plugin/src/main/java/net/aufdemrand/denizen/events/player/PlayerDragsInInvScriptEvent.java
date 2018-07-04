package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dInventory;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.objects.notable.NotableManager;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDragsInInvScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player drags in inventory (in_area <area>)
    // player drags (<item>) (in <inventory>) (in_area <area>)
    //
    // @Regex ^on player drags( ^[\s]+)?(in [^\s]+)?( in_area ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Cancellable true
    //
    // @Triggers when a player drags in an inventory.
    //
    // @Context
    // <context.item> returns the dItem the player has dragged.
    // <context.inventory> returns the dInventory.
    // <context.slots> returns a dList of the slot numbers dragged through.
    // <context.raw_slots> returns a dList of the raw slot numbers dragged through.
    //
    // -->

    public PlayerDragsInInvScriptEvent() {
        instance = this;
    }

    public static PlayerDragsInInvScriptEvent instance;

    public Inventory inventory;
    public dList slots;
    public dItem item;
    public dList raw_slots;
    private dPlayer entity;
    private dInventory dInv;
    private boolean manual_cancelled;
    public InventoryDragEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("player drags");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);

        String arg2 = CoreUtilities.getXthArg(2, lower);
        String arg3 = CoreUtilities.getXthArg(3, lower);
        String arg4 = CoreUtilities.getXthArg(4, lower);
        String inv = arg2.equals("in") ? arg3 : arg3.equals("in") ? arg4 : "";
        String nname = NotableManager.isSaved(dInv) ?
                CoreUtilities.toLowerCase(NotableManager.getSavedId(dInv)) :
                "\0";
        if (!inv.equals("") && !inv.equals("inventory")
                && !inv.equals(CoreUtilities.toLowerCase(dInv.getInventoryType().name()))
                && !inv.equals(CoreUtilities.toLowerCase(dInv.bestName()))
                && !(inv.equals("notable") && !nname.equals("\0"))
                && !inv.equals(nname)) {
            return false;
        }
        if (!arg2.equals("in") && !tryItem(item, arg2)) {
            return false;
        }
        return runInCheck(scriptContainer, s, lower, entity.getLocation(), "in_area");
    }

    @Override
    public String getName() {
        return "PlayerDragsInInventory";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        InventoryDragEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (CoreUtilities.toLowerCase(determination).equals("cancelled")) {
            manual_cancelled = true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(entity, null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("inventory")) {
            return dInv;
        }
        else if (name.equals("slots")) {
            return slots;
        }
        else if (name.equals("raw_slots")) {
            return raw_slots;
        }
        else if (name.equals("item")) {
            return item;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerDragsInInv(InventoryDragEvent event) {
        if (dEntity.isCitizensNPC(event.getWhoClicked())) {
            return;
        }
        entity = dEntity.getPlayerFrom(event.getWhoClicked());
        inventory = event.getInventory();
        dInv = dInventory.mirrorBukkitInventory(inventory);
        item = new dItem(event.getOldCursor());
        slots = new dList();
        for (Integer slot : event.getInventorySlots()) {
            slots.add(String.valueOf(slot + 1));
        }
        raw_slots = new dList();
        for (Integer raw_slot : event.getRawSlots()) {
            raw_slots.add(String.valueOf(raw_slot + 1));
        }
        cancelled = event.isCancelled();
        manual_cancelled = false;
        this.event = event;
        fire();
        if (cancelled && manual_cancelled) {
            final InventoryHolder holder = inventory.getHolder();
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.getPlayerEntity().updateInventory();
                    if (holder != null && holder instanceof Player) {
                        ((Player) holder).updateInventory();
                    }
                }
            }.runTaskLater(DenizenAPI.getCurrentInstance(), 1);
        }
        event.setCancelled(cancelled);
    }
}
