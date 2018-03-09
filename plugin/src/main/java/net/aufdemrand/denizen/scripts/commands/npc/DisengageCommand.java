package net.aufdemrand.denizen.scripts.commands.npc;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;

public class DisengageCommand extends AbstractCommand {

	/* DISENGAGE (NPCID:#) */

	/*
	 * Arguments: [] - Required, () - Optional (NPCID:#) Changes the Denizen
	 * affected to the Citizens2 NPCID specified
	 *
	 */

	@Override
	public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

		// Make sure NPC is available
		if (((BukkitScriptEntryData) scriptEntry.entryData).hasNPC()) {
			throw new InvalidArgumentsException("This command requires a linked NPC!");
		}

		// Parse arguments
		for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

			if (!scriptEntry.hasObject("player") && ((BukkitScriptEntryData) scriptEntry.entryData).hasPlayer()
					&& arg.matches("player")) {
				scriptEntry.addObject("player", new Element(true));
			}

			else {
				arg.reportUnhandled();
			}

		}

	}

	@Override
	public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

		dNPC npc = ((BukkitScriptEntryData) scriptEntry.entryData).getNPC();
		if (scriptEntry.hasObject("player")) {

			dPlayer player = ((BukkitScriptEntryData) scriptEntry.entryData).getPlayer();
			dB.report(scriptEntry, getName(), npc.debug() + player.debug());
			// Set Disengaged
			EngageCommand.setEngaged(npc.getCitizen(), player, false);
		}

		else {

			dB.report(scriptEntry, getName(), npc.debug());
			// Set Disengaged
			EngageCommand.setEngaged(npc.getCitizen(), false);
		}
	}
}
