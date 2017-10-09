package net.aufdemrand.denizen.objects.properties.item;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.NMSVersion;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemPatterns implements Property {

    public static boolean describes(dObject item) {
        if (item instanceof dItem) {
            Material material = ((dItem) item).getItemStack().getType();
            return material == Material.BANNER
                    || material == Material.WALL_BANNER
                    || material == Material.STANDING_BANNER
                    || (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_9_R2) && material == Material.SHIELD);
        }
        return false;
    }

    public static ItemPatterns getFrom(dObject item) {
        if (!describes(item)) {
            return null;
        }
        else {
            return new ItemPatterns((dItem) item);
        }
    }


    private ItemPatterns(dItem item) {
        this.item = item;
    }

    dItem item;

    private dList listPatterns() {
        dList list = new dList();
        for (Pattern pattern : getPatterns()) {
            list.add(pattern.getColor().name() + "/" + pattern.getPattern().name());
        }
        return list;
    }

    private List<Pattern> getPatterns() {
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta instanceof BlockStateMeta) {
            return ((Banner) ((BlockStateMeta) itemMeta).getBlockState()).getPatterns();
        }
        else {
            return ((BannerMeta) itemMeta).getPatterns();
        }
    }

    private void setPatterns(List<Pattern> patterns) {
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof BlockStateMeta) {
            Banner banner = (Banner) ((BlockStateMeta) itemMeta).getBlockState();
            banner.setPatterns(patterns);
            banner.update();
            ((BlockStateMeta) itemMeta).setBlockState(banner);
        }
        else {
            ((BannerMeta) itemMeta).setPatterns(patterns);
        }
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[language]
        // @name Pattern Color
        // @group Tags
        // @description
        // Possible colors for pattern
        // BLACK, BLUE, BROWN, CYAN, GRAY, GREEN,
        // LIGHT_BLUE, LIME, MAGENTA, ORANGE, PINK,
        // PURPLE, RED, SILVER, WHITE, YELLOW
        // -->

        // <--[language]
        // @name Patterns
        // @group Tags
        // @description
        // Possible patterns
        // BASE, BORDER, BRICKS, CIRCLE_MIDDLE, CREEPER, CROSS, CURLY_BORDER,
        // DIAGONAL_LEFT, DIAGONAL_LEFT_MIRROR, DIAGONAL_RIGHT, DIAGONAL_RIGHT_MIRROR,
        // FLOWER, GRADIENT, GRADIENT_UP, HALFT_HORIZONTAL,
        // HALF_HORIZONTAL_MIRROR, HALF_VERTICAL, HALF_VERTICAL_MIRROR, MOJANG,
        // RHOMBUS_MIDDLE, SKULL, SQUARE_BOTTOM_LEFT, SQUARE_BOTTOM_RIGHT,
        // SQUARE_TOP_LEFT, SQUARE_TOP_RIGHT, STRAIGHT_CROSS, STRIPE_BOTTUM,
        // STRIPE_CENTER, STRIPE_DOWNLEFT, STRIPE_DOWNRIGHT, STRIPE_LEFT,
        // STRIPE_MIDDLE, STRIPE_RIGHT, STRIPE_SMALL, STRIPE_TOP, TRIANGLE_BOTTOM,
        // TRIANGLE_TOP, TRIANGLES_BOTTOM, TRIANGLES_TOP
        // -->

        // <--[tag]
        // @attribute <i@item.patterns>
        // @returns dList
        // @group properties
        // @mechanism dItem.patterns
        // @description
        // Lists a banner's patterns in the form "li@COLOR/PATTERN|COLOR/PATTERN" etc.
        // For the list of possible colors, see <@link language Pattern Color>.
        // For the list of possible patterns, see <@link language Patterns>.
        // -->
        if (attribute.startsWith("patterns")) {
            return listPatterns().getAttribute(attribute.fulfill(1));
        }

        return null;
    }


    @Override
    public String getPropertyString() {
        dList list = listPatterns();
        if (list.isEmpty()) {
            return null;
        }
        return list.identify();
    }

    @Override
    public String getPropertyId() {
        return "patterns";
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dItem
        // @name patterns
        // @input dList
        // @description
        // Changes the patterns of a banner. Input must be in the form
        // "li@COLOR/PATTERN|COLOR/PATTERN" etc.
        // For the list of possible colors, see <@link language Pattern Color>.
        // For the list of possible patterns, see <@link language Patterns>.
        // @tags
        // <i@item.patterns>
        // -->

        if (mechanism.matches("patterns")) {
            List<Pattern> patterns = new ArrayList<Pattern>();
            dList list = mechanism.getValue().asType(dList.class);
            List<String> split;
            for (String string : list) {
                try {
                    split = CoreUtilities.split(string, '/', 2);
                    patterns.add(new Pattern(DyeColor.valueOf(split.get(0).toUpperCase()),
                            PatternType.valueOf(split.get(1).toUpperCase())));
                }
                catch (Exception e) {
                    dB.echoError("Could not apply pattern to banner: " + string);
                }
            }
            setPatterns(patterns);
        }
    }
}
