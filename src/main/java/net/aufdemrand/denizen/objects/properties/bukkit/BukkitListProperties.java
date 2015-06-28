package net.aufdemrand.denizen.objects.properties.bukkit;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class BukkitListProperties implements Property {
    public static boolean describes(dObject list) {
        return list instanceof dList;
    }

    public static BukkitListProperties getFrom(dObject list) {
        if (!describes(list)) return null;
        else return new BukkitListProperties((dList) list);
    }


    private BukkitListProperties(dList list) {
        this.list = list;
    }

    dList list;

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <li@list.formatted>
        // @returns Element
        // @description
        // returns the list in a human-readable format.
        // EG, a list of "n@3|p@bob|potato" will return "GuardNPC, bob, and potato".
        // -->
        if (attribute.startsWith("formatted")) {
            if (list.isEmpty()) return new Element("").getAttribute(attribute.fulfill(1));
            StringBuilder dScriptArg = new StringBuilder();

            for (int n = 0; n < list.size(); n++) {
                if (list.get(n).startsWith("p@")) {
                    dPlayer gotten = dPlayer.valueOf(list.get(n));
                    if (gotten != null) {
                        dScriptArg.append(gotten.getName());
                    }
                    else {
                        dScriptArg.append(list.get(n).replaceAll("\\w+@", ""));
                    }
                }
                else if (list.get(n).startsWith("e@") || list.get(n).startsWith("n@")) {
                    dEntity gotten = dEntity.valueOf(list.get(n));
                    if (gotten != null) {
                        dScriptArg.append(gotten.getName());
                    }
                    else {
                        dScriptArg.append(list.get(n).replaceAll("\\w+@", ""));
                    }
                }
                else {
                    dScriptArg.append(list.get(n).replaceAll("\\w+@", ""));
                }

                if (n == list.size() - 2) {
                    dScriptArg.append(n == 0 ? " and " : ", and ");
                }
                else {
                    dScriptArg.append(", ");
                }
            }

            return new Element(dScriptArg.toString().substring(0, dScriptArg.length() - 2))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <fl@flag_name.expiration>
        // @returns Duration
        // @description
        // returns a Duration of the time remaining on the flag, if it
        // has an expiration.
        // -->
        if (list.flag != null && attribute.startsWith("expiration")) {
            return DenizenAPI.getCurrentInstance().getFlag(list.flag).expiration()
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "BukkitListProperties";
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }
}
