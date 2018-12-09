package net.aufdemrand.denizen.objects;

import net.aufdemrand.denizen.tags.BukkitTagContext;
import net.aufdemrand.denizencore.objects.*;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.objects.properties.PropertyParser;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.regex.Matcher;

public class dTradeRecipe implements dObject, Adjustable {

    //////////////////
    //    OBJECT FETCHER
    ////////////////

    public static dTradeRecipe valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("traderecipe")
    public static dTradeRecipe valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ///////
        // Handle objects with properties through the object fetcher
        Matcher m = ObjectFetcher.DESCRIBED_PATTERN.matcher(string);
        if (m.matches()) {
            return ObjectFetcher.getObjectFrom(dTradeRecipe.class, string, new BukkitTagContext(((BukkitTagContext) context).player,
                    ((BukkitTagContext) context).npc, false, null, !context.debug, null));
        }

        string = CoreUtilities.toLowerCase(string).replace("traderecipe@", "");
        if (string.toLowerCase().matches("recipe")) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.AIR), 0);
            recipe.addIngredient(new ItemStack(Material.AIR));
            return new dTradeRecipe(recipe);
        }
        return null;
    }

    public static boolean matches(String arg) {
        return arg.replace("traderecipe@", "").matches("recipe");
    }

    ///////////////
    //   Constructors
    /////////////

    public dTradeRecipe(MerchantRecipe recipe) {
        this.recipe = recipe;
    }

    /////////////////////
    //   INSTANCE FIELDS/METHODS
    /////////////////

    public String toString() {
        return identify();
    }

    private MerchantRecipe recipe;

    public MerchantRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(MerchantRecipe recipe) {
        this.recipe = recipe;
    }

    //////////////////////////////
    //  DSCRIPT ARGUMENT METHODS
    /////////////////////////

    private String prefix = "traderecipe";

    public String getPrefix() {
        return prefix;
    }

    public dTradeRecipe setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String debug() {
        return prefix + "='<A>" + identify() + "<G>'  ";
    }

    public boolean isUnique() {
        return false;
    }

    public String getObjectType() {
        return "TradeRecipe";
    }

    public String identify() {
        return prefix + "@recipe" + PropertyParser.getPropertiesString(this);
    }

    public String identifySimple() {
        return identify();
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        for (Property property : PropertyParser.getProperties(this)) {
            String returned = property.getAttribute(attribute);
            if (returned != null) {
                return returned;
            }
        }

        return new Element(identify()).getAttribute(attribute);
    }

    public void applyProperty(Mechanism mechanism) {
        adjust(mechanism);
    }

    public void adjust(Mechanism mechanism) {
        for (Property property : PropertyParser.getProperties(this)) {
            property.adjust(mechanism);
            if (mechanism.fulfilled()) {
                break;
            }
        }

        if (!mechanism.fulfilled()) {
            mechanism.reportInvalid();
        }
    }
}
