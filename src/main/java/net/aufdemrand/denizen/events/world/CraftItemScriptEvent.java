package net.aufdemrand.denizen.events.world;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.*;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftItemScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // item crafted (at:<location>) (in <area>)
    // <item> crafted (at:<location>) (in <area>)
    // <material> crafted (at:<location>) (in <area>)
    //
    // @Regex ^on [^\s]+ crafted( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Switch at <Location value>
    //
    // @Cancellable true
    //
    // @Triggers when an item's recipe is correctly formed.
    //
    // @Context
    // <context.inventory> returns the dInventory of the crafting inventory.
    // <context.item> returns the dItem to be crafted.
    // <context.recipe> returns a dList of dItems in the recipe.
    //
    // @Determine
    // dItem to change the item that is crafted.
    //
    // -->
    // <--[event]
    // @Events

    public CraftItemScriptEvent() {
        instance = this;
    }

    public static CraftItemScriptEvent instance;
    public CraftingInventory inventory;
    public dItem item;
    public dList recipe;
    private dLocation location;
    private Boolean iUpdated;
    public PrepareItemCraftEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(1, lower);
        dB.log("Adding in hanlders for CraftItem");
        return cmd.equals("crafted");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cItem = CoreUtilities.getXthArg(0, lower);
        dB.log("string: "+lower);
        dB.log("Location: "+location);

        if (!tryItem(item, cItem)) {
            return false;
        }

        if (!runAtCheck(scriptContainer, s, lower, location)) {
            return false;
        }

        return runInCheck(scriptContainer, s, CoreUtilities.toLowerCase(s), location);
    }

    @Override
    public String getName() {
        return "CraftItemScriptEvent";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PrepareItemCraftEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {

        if (determination.toUpperCase().startsWith("CANCELLED")) {
            inventory.setResult(null);
            iUpdated = true;
        }
        else if (dItem.matches(determination)) {
            inventory.setResult(dItem.valueOf(determination).getItemStack());
            iUpdated = true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event.getView().getPlayer() != null ? dEntity.getPlayerFrom(event.getView().getPlayer()) : null, null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("inventory")) {
            return (dInventory) inventory;
        }
        else if (name.equals("item")) {
            return item;
        }
        else if (name.equals("recipe")) {
            return recipe;
        }
        return super.getContext(name);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraftItem(PrepareItemCraftEvent event) {
        dB.log("<red>Entering CraftItem Event");
        inventory = event.getInventory();
        Player holder = (Player) event.getView().getPlayer();
        location = ((dPlayer) holder).getEyeLocation();
        dB.log("Location: "+location);
        Recipe erecipe = event.getRecipe();
        item = erecipe.getResult() != null ? new dItem(erecipe.getResult()) : null;
        for (ItemStack ritem : inventory.getMatrix()) {
            if (ritem != null)
                recipe.add(new dItem(ritem).identify());
            else
                recipe.add(new dItem(Material.AIR).identify());
        }
        iUpdated = false;
        this.event = event;
        fire();
        if (iUpdated) {
            holder.updateInventory();
        }
    }
}
