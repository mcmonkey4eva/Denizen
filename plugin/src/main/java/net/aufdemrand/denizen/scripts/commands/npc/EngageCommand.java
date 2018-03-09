package net.aufdemrand.denizen.scripts.commands.npc;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.Settings;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Duration;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.citizensnpcs.api.npc.NPC;

import java.util.HashMap;
import java.util.Map;

public class EngageCommand extends AbstractCommand {

	@Override
	public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

		// Check for NPC
		if (!((BukkitScriptEntryData) scriptEntry.entryData).hasNPC()) {
			throw new InvalidArgumentsException("This command requires a linked NPC!");
		}

		// Parse arguments
		for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

			if (!scriptEntry.hasObject("player") && ((BukkitScriptEntryData) scriptEntry.entryData).hasPlayer()) {
				scriptEntry.addObject("player", new Element(true));
			}

			else if (!scriptEntry.hasObject("duration") && arg.matchesArgumentType(Duration.class)) {
				scriptEntry.addObject("duration", arg.asType(Duration.class));
			}

			else {
				arg.reportUnhandled();
			}

		}

		scriptEntry.defaultObject("duration", new Duration(0));

	}

	@Override
	public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

		Duration duration = scriptEntry.getdObject("duration");
		dNPC npc = ((BukkitScriptEntryData) scriptEntry.entryData).getNPC();

		// Report to dB
		if (!scriptEntry.hasObject("player")) {
			dB.report(scriptEntry, getName(), npc.debug() + duration.debug());

			if (duration.getSecondsAsInt() > 0) {
				setEngaged(npc.getCitizen(), duration.getSecondsAsInt());
			} else {
				setEngaged(npc.getCitizen(), true);
			}
		}

		else {
			dPlayer player = ((BukkitScriptEntryData) scriptEntry.entryData).getPlayer();
			dB.report(scriptEntry, getName(), npc.debug() + player.debug() + duration.debug());

			if (duration.getSecondsAsInt() > 0) {
				setEngaged(npc.getCitizen(), player, duration.getSecondsAsInt());
			} else {
				setEngaged(npc.getCitizen(), player, true);
			}
		}

	}

    /*
     * Engaged NPCs cannot interact with Players
     */
    private static Map<NPC, Long> currentlyEngaged = new HashMap<NPC, Long>();
    private static Map<String, Long> currentlyEngagedPlayer = new HashMap<String, Long>();
    
    public static String getHashKey(NPC npc, dPlayer player) {
    	return npc.getId() + "." + player;
    }

    /**
     * Checks if the dNPC is ENGAGED. Engaged NPCs do not respond to
     * Player interaction.
     *
     * @param npc the Denizen NPC being checked
     * @param dPlayer Player being checked (optional)
     * @return if the dNPC is currently engaged
     */
	public static boolean getEngaged(NPC npc) {
		if (currentlyEngaged.containsKey(npc)) {
			return currentlyEngaged.get(npc) > System.currentTimeMillis();
		}
		return false;
	}

	public static boolean getEngaged(NPC npc, dPlayer player) {
		if (currentlyEngaged.containsKey(npc)) {
			return currentlyEngaged.get(npc) > System.currentTimeMillis();
		} else if (currentlyEngagedPlayer.containsKey(getHashKey(npc, player))) {
			return currentlyEngagedPlayer.get(getHashKey(npc, player)) > System.currentTimeMillis();
		}
		return false;
	}

    /**
     * Sets a dNPC's ENGAGED status. Engaged NPCs do not respond to Player
     * interaction. Note: Denizen NPC will automatically disengage after the
     * engage_timeout_in_seconds which is set in the Denizen config.yml.
     *
     * @param npc     the dNPC affected
     * @param player  the dPlayer affected (optional)
     * @param engaged true sets the dNPC engaged, false sets the dNPC as disengaged
     */
	public static void setEngaged(NPC npc, boolean engaged) {
		if (engaged) {
			currentlyEngaged.put(npc, System.currentTimeMillis()
					+ (long) (Duration.valueOf(Settings.engageTimeoutInSeconds()).getSeconds()) * 1000);
		}
		if (!engaged) {
			currentlyEngaged.remove(npc);
		}
	}

	public static void setEngaged(NPC npc, dPlayer player, boolean engaged) {
		if (engaged) {
			currentlyEngagedPlayer.put(getHashKey(npc, player), System.currentTimeMillis()
					+ (long) (Duration.valueOf(Settings.engageTimeoutInSeconds()).getSeconds()) * 1000);
		}
		if (!engaged) {
			currentlyEngagedPlayer.remove(getHashKey(npc, player));
		}
	}

    /**
     * Sets a dNPC as ENGAGED for a specific amount of seconds. Engaged NPCs do not
     * respond to Player interaction. If the NPC is previously engaged, using this will
     * over-ride the previously set duration.
     *
     * @param npc      the dNPC to set as engaged
     * @param player   the dPlayer affected (optional)
     * @param duration the number of seconds to engage the dNPC
     */
	public static void setEngaged(NPC npc, int duration) {
		currentlyEngaged.put(npc, System.currentTimeMillis() + duration * 1000);
	}

	public static void setEngaged(NPC npc, dPlayer player, int duration) {
		currentlyEngagedPlayer.put(getHashKey(npc, player), System.currentTimeMillis() + duration * 1000);
	}
}
