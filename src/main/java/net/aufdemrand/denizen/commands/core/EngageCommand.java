package net.aufdemrand.denizen.commands.core;

import java.util.HashMap;
import java.util.Map;

import net.aufdemrand.denizen.commands.AbstractCommand;
import net.aufdemrand.denizen.npc.DenizenNPC;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.command.exception.CommandException;

/**
 * Sets/unsets the Denizen's built in Engage List. 
 * When ENGAGEd, a Denizen will not interact with a Player until DISENGAGEd (or timed out).
 * 
 * @author Jeremy Schroeder
 */

public class EngageCommand extends AbstractCommand {

	/* ENGAGE (# of Seconds) */

	/* Arguments: [] - Required, () - Optional 
	 * (# of Seconds) Will automatically DISENGAGE after specified amount of seconds.
	 *   If not set, the Denizen will remain ENGAGEd until a DISENGAGE command is used.
	 *   
	 * Modifiers:
	 * (NPCID:#) Changes the Denizen affected to the Citizens2 NPCID specified
	 */

	/* DISENGAGE */

	/* Arguments: [] - Required, () - Optional
	 * None.
	 * 
	 * Modifiers:
	 * (NPCID:#) Changes the Denizen affected to the Citizens2 NPCID specified
	 */

	@Override
	public boolean execute(ScriptEntry theCommand) throws CommandException {

		/* Initialize variables */ 

		Integer timedEngage = null;
		DenizenNPC theDenizen = theCommand.getDenizen();

		/* Get arguments */
		if (theCommand.arguments() != null) {
			for (String thisArgument : theCommand.arguments()) {
				if (thisArgument.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
					timedEngage = Integer.valueOf(thisArgument);

				if (thisArgument.toUpperCase().contains("NPCID:"))
					try {
						if (CitizensAPI.getNPCRegistry().getById(Integer.valueOf(thisArgument.split(":")[1])) != null)
							theDenizen = plugin.getDenizenNPCRegistry().getDenizen(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(thisArgument.split(":")[1])));	
					} catch (Throwable e) {
						throw new CommandException("NPCID specified could not be matched to a Denizen.");
					}
			}	
		}

		/* If a DISENGAGE, take the Denizen out of the engagedList. */
		if (theCommand.getCommand().equalsIgnoreCase("DISENGAGE")) {
			setEngaged(theCommand.getDenizen(), false);
			return true;
		}

		/* ENGAGE the Denizen. */
		if (timedEngage != null) 
			setEngaged(theDenizen, timedEngage);
		else 			
			setEngaged(theCommand.getDenizen(), true);

		return true;
	}


	/* Engaged NPCs cannot interact with Players */

	private Map<DenizenNPC, Long> engagedNPC = new HashMap<DenizenNPC, Long>();

	public boolean getEngaged(NPC theDenizen) {
		if (engagedNPC.containsKey(theDenizen)) 
			if (engagedNPC.get(theDenizen) > System.currentTimeMillis())
				return true;
		return false;
	}

	public void setEngaged(DenizenNPC theDenizen, boolean engaged) {
		if (engaged) engagedNPC.put(theDenizen, System.currentTimeMillis() 
				+ plugin.settings.EngageTimeoutInSeconds() * 1000 );
		if (!engaged) engagedNPC.remove(theDenizen);
	}

	public void setEngaged(DenizenNPC theDenizen, Integer duration) {
		engagedNPC.put(theDenizen, System.currentTimeMillis() + duration * 1000 );

	}


}