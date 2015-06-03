package net.aufdemrand.denizen.events.scriptevents;

import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;

import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.HashMap;

public class ListPingScriptEvent extends ScriptEvent  implements Listener {
    // <--[event]
    // @Events
    // server list ping
    //
    // @Triggers when the server is pinged for a client's server list.
    // @Context
    // <context.motd> returns the MOTD that will show.
    // <context.max_players> returns the number of max players that will show.
    // <context.num_players> returns the number of online players that will show.
    // <context.address> returns the IP address requesting the list.
    //
    // @Determine
    // Element(Number) to change the max player amount that will show
    // Element(Number)|Element (String) to set the max player amount and change the MOTD.
    // Element(String) to change the MOTD that will show.
    //
    // -->
    public ListPingScriptEvent() {
        instance = this;
    }
    public static ListPingScriptEvent instance;
    public Element motd;
    public Element max_players;
    public Element num_players;
    public Element address;
    public ServerListPingEvent event;

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
        return "ListPing";
    }

    @Override
    public void init() { Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance()); }

    @Override
    public void destroy() {
        BlockRedstoneEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        String[] values = determination.split("[\\|" + dList.internal_escape + "]", 2);
        if (new Element(values[0]).isInt())
            event.setMaxPlayers(new Element(values[0]).asInt());
        if (determination.length() > 0 && !determination.equalsIgnoreCase("none")) {
            if (values.length == 2)
                event.setMotd(values[1]);
            else
                event.setMotd(determination);
        }
        return true;
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("motd", motd);
        context.put("max_players", max_players);
        context.put("num_players", num_players);
        context.put("address", address);
        return context;
    }

    @EventHandler
    public void onListPing(ServerListPingEvent event) {
        motd = new Element(event.getMotd());
        max_players = new Element(event.getMaxPlayers());
        num_players = new Element(event.getNumPlayers());
        address = new Element(event.getAddress().toString());
        this.event = event;
        fire();
    }
}
