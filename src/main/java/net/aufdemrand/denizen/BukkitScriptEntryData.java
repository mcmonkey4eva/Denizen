package net.aufdemrand.denizen;

import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.tags.BukkitTagContext;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.tags.TagContext;

public class BukkitScriptEntryData extends ScriptEntryData {
    private dPlayer player;
    private dNPC npc;

    public BukkitScriptEntryData(dPlayer player, dNPC npc) {
        this.player = player;
        this.npc = npc;
    }

    public dPlayer getPlayer() {
        return player;
    }

    public dNPC getNPC() {
        return npc;
    }

    public boolean hasNPC() {
        return npc != null;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public void setPlayer(dPlayer player) {
        this.player = player;
    }

    public void setNPC(dNPC npc) {
        this.npc = npc;
    }

    @Override
    public void transferDataFrom(ScriptEntryData scriptEntryData) {
        if (scriptEntryData == null) {
            return;
        }
        player = ((BukkitScriptEntryData) scriptEntryData).getPlayer();
        npc = ((BukkitScriptEntryData) scriptEntryData).getNPC();

    }

    @Override
    public TagContext getTagContext() {
        return new BukkitTagContext(player, npc, false, null, true, null);
    }

    @Override
    public String toString() {
        if (npc == null && player == null) {
            return "";
        }
        else if (npc == null) {
            return "player=p@" + player.getName();
        }
        else if (player == null) {
            return "npc=n@" + npc.getId();
        }
        else {
            return "player=p@" + player.getName() + "   npc=n@" + npc.getId();
        }
    }
}
