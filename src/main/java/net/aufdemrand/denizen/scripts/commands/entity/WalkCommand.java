package net.aufdemrand.denizen.scripts.commands.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.depends.Depends;
import net.aufdemrand.denizen.utilities.entity.EntityMovement;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.aufdemrand.denizencore.scripts.commands.Holdable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalkCommand extends AbstractCommand implements Holdable {

    //                        percentage
    // walk [location] (speed:#.#) (auto_range)
    //

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        // Interpret arguments

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("location")
                    && arg.matchesArgumentType(dLocation.class))
                scriptEntry.addObject("location", arg.asType(dLocation.class));

            else if (!scriptEntry.hasObject("speed")
                    && arg.matchesPrimitive(aH.PrimitiveType.Percentage)
                    && arg.matchesPrefix("s, speed"))
                scriptEntry.addObject("speed", arg.asElement());

            else if (!scriptEntry.hasObject("auto_range")
                    && arg.matches("auto_range"))
                scriptEntry.addObject("auto_range", Element.TRUE);

            else if (!scriptEntry.hasObject("radius")
                    && arg.matchesPrimitive(aH.PrimitiveType.Double)
                    && arg.matchesPrefix("radius"))
                scriptEntry.addObject("radius", arg.asElement());

            else if (!scriptEntry.hasObject("stop")
                    && arg.matches("stop"))
                scriptEntry.addObject("stop", new Element(true));

            else if (!scriptEntry.hasObject("entities")
                    && arg.matchesArgumentList(dEntity.class))
                scriptEntry.addObject("entities", arg.asType(dList.class).filter(dEntity.class));

            else
                arg.reportUnhandled();
        }


        // Check for required information

        if (!scriptEntry.hasObject("location") && !scriptEntry.hasObject("stop"))
            throw new InvalidArgumentsException("Must specify a location!");

        if (!scriptEntry.hasObject("entities")) {
            if (((BukkitScriptEntryData) scriptEntry.entryData).getNPC() == null
                    || !((BukkitScriptEntryData) scriptEntry.entryData).getNPC().isValid()
                    || !((BukkitScriptEntryData) scriptEntry.entryData).getNPC().isSpawned())
                throw new InvalidArgumentsException("Must have a valid spawned NPC attached.");
            else
                scriptEntry.addObject("entities",
                        Arrays.asList(((BukkitScriptEntryData) scriptEntry.entryData).getNPC().getDenizenEntity()));
        }

        scriptEntry.defaultObject("stop", new Element(false));
    }


    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

        // Fetch required objects

        dLocation loc = (dLocation) scriptEntry.getObject("location");
        Element speed = scriptEntry.getElement("speed");
        Element auto_range = scriptEntry.getElement("auto_range");
        Element radius = scriptEntry.getElement("radius");
        Element stop = scriptEntry.getElement("stop");
        List<dEntity> entities = (List<dEntity>) scriptEntry.getObject("entities");


        // Debug the execution

        dB.report(scriptEntry, getName(), (loc != null ? loc.debug() : "")
                + (speed != null ? speed.debug() : "")
                + (auto_range != null ? auto_range.debug() : "")
                + (radius != null ? radius.debug() : "")
                + stop.debug()
                + (aH.debugObj("entities", entities)));

        // Do the execution

        boolean shouldStop = stop.asBoolean();

        List<dNPC> npcs = new ArrayList<dNPC>();
        final List<dEntity> waitForEntities = new ArrayList<dEntity>();
        for (final dEntity entity : entities) {
            if (entity.isCitizensNPC()) {
                dNPC npc = entity.getDenizenNPC();
                npcs.add(npc);
                if (!npc.isSpawned()) {
                    dB.echoError(scriptEntry.getResidingQueue(), "NPC " + npc.identify() + " is not spawned!");
                    continue;
                }

                if (shouldStop) {
                    npc.getNavigator().cancelNavigation();
                    continue;
                }

                if (auto_range != null
                        && auto_range == Element.TRUE) {
                    double distance = npc.getLocation().distance(loc);
                    if (npc.getNavigator().getLocalParameters().range() < distance + 10)
                        npc.getNavigator().getLocalParameters().range((float) distance + 10);
                }

                npc.getNavigator().setTarget(loc);

                if (speed != null)
                    npc.getNavigator().getLocalParameters().speedModifier(speed.asFloat());

                if (radius != null) {
                    npc.getNavigator().getLocalParameters().addRunCallback(WalkCommandCitizensEvents
                            .generateNewFlocker(npc.getCitizen(), radius.asDouble()));
                }
            }
            else if (shouldStop) {
                EntityMovement.stopWalking(entity.getBukkitEntity());
            }
            else {
                waitForEntities.add(entity);
                EntityMovement.walkTo(entity.getBukkitEntity(), loc, speed != null ? speed.asDouble() : 0.2,
                        new Runnable() {
                            @Override
                            public void run() {
                                checkHeld(entity);
                            }
                        });
            }
        }

        if (scriptEntry.shouldWaitFor()) {
            held.add(scriptEntry);
            if (!npcs.isEmpty())
                scriptEntry.addObject("tally", npcs);
            if (!waitForEntities.isEmpty())
                scriptEntry.addObject("entities", waitForEntities);
        }

    }


    // Held script entries
    public static List<ScriptEntry> held = new ArrayList<ScriptEntry>();

    public void checkHeld(dEntity entity) {
        for (int i = 0; i < held.size(); i++) {
            ScriptEntry entry = held.get(i);
            List<dEntity> waitForEntities = (List<dEntity>) entry.getObject("entities");
            waitForEntities.remove(entity);
            if (waitForEntities.isEmpty()) {
                if (!entry.hasObject("tally") || ((List<dNPC>) entry.getObject("tally")).isEmpty()) {
                    entry.setFinished(true);
                    held.remove(i);
                    i--;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (Depends.citizens != null) {
            DenizenAPI.getCurrentInstance().getServer().getPluginManager()
                    .registerEvents(new WalkCommandCitizensEvents(), DenizenAPI.getCurrentInstance());
        }
    }
}
