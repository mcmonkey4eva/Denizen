package net.aufdemrand.denizen.tags.core;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.notable.NotableManager;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.TagRunnable;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.ReplaceableTagEvent;
import net.aufdemrand.denizencore.tags.TagManager;
import org.bukkit.event.Listener;

@Deprecated
public class NotableLocationTags {

    public NotableLocationTags(Denizen denizen) {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                notableTags(event);
            }
        }, "notable");
    }

    public void notableTags(ReplaceableTagEvent event) {

        if (!event.matches("notable")) {
            return;
        }

        dB.echoError(event.getAttributes().getScriptEntry().getResidingQueue(), "notable: tags are deprecated! Use <l@NotableName>!");
        String tag = event.raw_tag;

        String id = null;
        if (event.hasValue()) {
            id = event.getValue();
            tag = tag.split(":", 2)[1];
        }
        else if (event.hasNameContext()) {
            id = event.getNameContext();
        }

        if (NotableManager.isType(id, dLocation.class)) {
            dB.echoError("Notable tag '" + event.raw_tag + "': id was not found.");
        }

        dLocation location = (dLocation) NotableManager.getSavedObject(id);

        Attribute attribute = event.getAttributes();
        attribute.fulfill(1);
        tag = location.getAttribute(attribute);

        event.setReplaced(tag);

    }
}
