package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;

import java.util.HashMap;

public class CreeperPoweredScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // creeper powered (because <cause>)
    //
    // @Cancellable true
    //
    // @Triggers when a creeper is struck by lightning and turned into a powered creeper.
    //
    // @Context
    // <context.entity> returns the dEntity of the creeper.
    // <context.lightning> returns the dEntity of the lightning.
    // <context.cause> returns an Element of the cause for the creeper being powered.
    //
    // -->

    public CreeperPoweredScriptEvent() {
        instance = this;
    }
    public static CreeperPoweredScriptEvent instance;
    public dEntity lightning;
    public dEntity entity;
    public Element cause;
    public CreeperPowerEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("creeper powered");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String reason = CoreUtilities.getXthArg(3,CoreUtilities.toLowerCase(s));
        return reason.length() == 0 || reason.equals(cause.toString());
    }

    @Override
    public String getName() {
        return "CreeperPowered";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        CreeperPowerEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        if (lightning != null) {
            context.put("lightning", lightning);
        }
        context.put("entity", entity);
        context.put("cause", cause);
        return context;
    }

    @EventHandler
    public void onCreeperPowered(CreeperPowerEvent event) {
        lightning = new dEntity(event.getLightning());
        entity = new dEntity(event.getEntity());
        cause = new Element(event.getCause().name());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
