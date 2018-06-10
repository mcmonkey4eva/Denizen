package net.aufdemrand.denizen.scripts.commands.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HurtCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        boolean specified_targets = false;

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("amount")
                    && (arg.matchesPrimitive(aH.PrimitiveType.Double)
                    || arg.matchesPrimitive(aH.PrimitiveType.Integer))) {
                scriptEntry.addObject("amount", arg.asElement());
            }
            else if (!scriptEntry.hasObject("source")
                    && arg.matchesPrefix("source", "s")
                    && arg.matchesArgumentType(dEntity.class)) {
                scriptEntry.addObject("source", arg.asType(dEntity.class));
            }
            else if (!scriptEntry.hasObject("entities")
                    && arg.matchesArgumentType(dList.class)) {
                // Entity arg
                scriptEntry.addObject("entities", arg.asType(dList.class).filter(dEntity.class));
                specified_targets = true;
            }
            else if (!scriptEntry.hasObject("entities")
                    && arg.matchesArgumentType(dEntity.class)) {
                // Entity arg
                scriptEntry.addObject("entities", Arrays.asList(arg.asType(dEntity.class)));
                specified_targets = true;
            }
            else if (!scriptEntry.hasObject("cause")
                    && arg.matchesEnum(EntityDamageEvent.DamageCause.values())) {
                scriptEntry.addObject("cause", arg.asElement());
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("amount")) {
            scriptEntry.addObject("amount", new Element(1.0d));
        }

        if (!specified_targets) {
            List<dEntity> entities = new ArrayList<dEntity>();
            if (((BukkitScriptEntryData) scriptEntry.entryData).getPlayer() != null) {
                entities.add(((BukkitScriptEntryData) scriptEntry.entryData).getPlayer().getDenizenEntity());
            }
            else if (((BukkitScriptEntryData) scriptEntry.entryData).getNPC() != null) {
                entities.add(((BukkitScriptEntryData) scriptEntry.entryData).getNPC().getDenizenEntity());
            }
            else {
                throw new InvalidArgumentsException("No valid target entities found.");
            }
            scriptEntry.addObject("entities", entities);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

        List<dEntity> entities = (List<dEntity>) scriptEntry.getObject("entities");
        dEntity source = (dEntity) scriptEntry.getObject("source");
        Element amountElement = scriptEntry.getElement("amount");
        Element cause = scriptEntry.getElement("cause");

        dB.report(scriptEntry, getName(), amountElement.debug()
                + aH.debugList("entities", entities)
                + (cause == null ? "" : cause.debug())
                + (source == null ? "" : source.debug()));

        double amount = amountElement.asDouble();
        for (dEntity entity : entities) {
            if (entity.getLivingEntity() == null) {
                dB.echoDebug(scriptEntry, entity + " is not a living entity!");
                continue;
            }
            if (cause == null) {
                if (source == null) {
                    entity.getLivingEntity().damage(amount);
                }
                else {
                    entity.getLivingEntity().damage(amount, source.getBukkitEntity());
                }
            }
            else {
                EntityDamageEvent ede = source == null ? new EntityDamageEvent(entity.getBukkitEntity(),
                        EntityDamageEvent.DamageCause.valueOf(cause.asString().toUpperCase()), amount) :
                        new EntityDamageByEntityEvent(source.getBukkitEntity(),
                                entity.getBukkitEntity(), EntityDamageEvent.DamageCause.valueOf(cause.asString().toUpperCase()), amount);
                Bukkit.getPluginManager().callEvent(ede);
                if (!ede.isCancelled()) {
                    if (source == null) {
                        entity.getLivingEntity().damage(ede.getFinalDamage());
                    }
                    else {
                        entity.getLivingEntity().damage(ede.getFinalDamage(), source.getBukkitEntity());
                    }
                }
            }
        }
    }
}
