package net.aufdemrand.denizen.scripts.commands.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.objects.dTrade;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenTradesCommand extends AbstractCommand {

    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("trades")
                    && (arg.matchesArgumentType(dTrade.class)
                    || arg.matchesArgumentType(dList.class))) {
                scriptEntry.addObject("trades", arg.asType(dList.class).filter(dTrade.class));
            }
            else if (arg.matchesPrefix("title")) {
                scriptEntry.addObject("title", arg.asElement());
            }
            else if (arg.matchesPrefix("players")
                    && (arg.matchesArgumentType(dPlayer.class)
                    || arg.matchesArgumentType(dList.class))) {
                scriptEntry.addObject("players", arg.asType(dList.class).filter(dPlayer.class));
            }
            else {
                arg.reportUnhandled();
            }

        }

        if (!scriptEntry.hasObject("trades")) {
            throw new InvalidArgumentsException("Must have at least one trade!");
        }

        scriptEntry.defaultObject("title", new Element(""))
                .defaultObject("players", Arrays.asList(((BukkitScriptEntryData) scriptEntry.entryData).getPlayer()));

    }

    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

        String title = scriptEntry.getElement("title").asString();
        List<dTrade> trades = (List<dTrade>) scriptEntry.getObject("trades");
        List<dPlayer> players = (List<dPlayer>) scriptEntry.getObject("players");

        if (scriptEntry.dbCallShouldDebug()) {

            dB.report(scriptEntry, getName(),
                    aH.debugList("trades", trades)
                            + aH.debugObj("title", title)
                            +  aH.debugList("players", players));

        }

        List<MerchantRecipe> recipes = new ArrayList<>();
        for (dTrade trade : trades) {
            recipes.add(trade.getRecipe());
        }

        for (dPlayer player : players) {
            Merchant merchant = Bukkit.createMerchant(title);
            merchant.setRecipes(recipes);
            player.getPlayerEntity().openMerchant(merchant, true);
        }

    }
}
