package net.aufdemrand.denizen.scripts.commands.npc;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.npc.traits.SittingTrait;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Wolf;

public class SitCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {
            if (arg.matchesArgumentType(dLocation.class)
                    && !scriptEntry.hasObject("location")) {
                scriptEntry.addObject("location", arg.asType(dLocation.class));
            }
            else {
                arg.reportUnhandled();
            }
        }
        if (!((BukkitScriptEntryData) scriptEntry.entryData).hasNPC()) {
            throw new InvalidArgumentsException("This command requires a linked NPC!");
        }

    }

    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {
    	
        dLocation location = (dLocation) scriptEntry.getObject("location");
    	dNPC npc = ((BukkitScriptEntryData) scriptEntry.entryData).getNPC();
    	EntityType npcType = npc.getEntityType();
        
        if (npcType != EntityType.PLAYER
                && npcType != EntityType.OCELOT
                && npcType != EntityType.WOLF) {
            dB.echoError(scriptEntry.getResidingQueue(), "...only Player, ocelot, or wolf type NPCs can sit!");
            return;
        }

        dB.report(scriptEntry, getName(), aH.debugObj("npc", npc)
                + (location != null ? location.debug() : ""));

        if (npcType == EntityType.OCELOT) {
            ((Ocelot) npc.getEntity()).setSitting(true);
        }
        else if (npcType == EntityType.WOLF) {
            ((Wolf) npc.getEntity()).setSitting(true);
        }
        else {
            NPC citizen = npc.getCitizen();
            SittingTrait trait = citizen.getTrait(SittingTrait.class);
            
            if (!citizen.hasTrait(SittingTrait.class)) {
            	 citizen.addTrait(SittingTrait.class);
                 dB.echoDebug(scriptEntry, "...added sitting trait");
            }

            if (location == null) {
                trait.sit();
            }
            else {
                trait.sit(location);
            }
        }
    }
}
