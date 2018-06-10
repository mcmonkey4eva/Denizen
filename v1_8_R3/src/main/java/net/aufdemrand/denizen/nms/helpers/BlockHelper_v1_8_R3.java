package net.aufdemrand.denizen.nms.helpers;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.aufdemrand.denizen.nms.impl.blocks.BlockData_v1_8_R3;
import net.aufdemrand.denizen.nms.impl.jnbt.CompoundTag_v1_8_R3;
import net.aufdemrand.denizen.nms.interfaces.BlockData;
import net.aufdemrand.denizen.nms.interfaces.BlockHelper;
import net.aufdemrand.denizen.nms.util.PlayerProfile;
import net.aufdemrand.denizen.nms.util.jnbt.CompoundTag;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.material.MaterialData;

import java.util.UUID;

public class BlockHelper_v1_8_R3 implements BlockHelper {

    @Override
    public MaterialData getFlowerpotContents(Block block) {
        TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) ((CraftWorld) block.getWorld()).getHandle().getTileEntity(
                new BlockPosition(block.getX(), block.getY(), block.getZ()));
        ItemStack is = new ItemStack(flowerPot.b());
        return new MaterialData(CraftItemStack.asBukkitCopy(is).getType(), (byte) flowerPot.c());
    }

    @Override
    public void setFlowerpotContents(Block block, MaterialData data) {
        TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) ((CraftWorld) block.getWorld()).getHandle().getTileEntity(
                new BlockPosition(block.getX(), block.getY(), block.getZ()));
        ItemStack contents = CraftItemStack.asNMSCopy(data.toItemStack());
        if (contents == null) {
            flowerPot.a(null, 0);
        }
        else {
            flowerPot.a(contents.getItem(), contents.getData());
        }

        block.getState().update();
    }

    @Override
    public PlayerProfile getPlayerProfile(Skull skull) {
        GameProfile profile = ((CraftSkull) skull).getTileEntity().getGameProfile();
        if (profile == null) {
            return null;
        }
        String name = profile.getName();
        UUID id = profile.getId();
        Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
        return new PlayerProfile(name, id, property != null ? property.getValue() : null);
    }

    @Override
    public void setPlayerProfile(Skull skull, PlayerProfile playerProfile) {
        GameProfile gameProfile = new GameProfile(playerProfile.getUniqueId(), playerProfile.getName());
        if (playerProfile.hasTexture()) {
            gameProfile.getProperties().put("textures",
                    new Property("textures", playerProfile.getTexture(), playerProfile.getTextureSignature()));
        }
        TileEntitySkull tileEntity = ((CraftSkull) skull).getTileEntity();
        tileEntity.setSkullType(SkullType.PLAYER.ordinal());
        tileEntity.setGameProfile(gameProfile);
        skull.getBlock().getState().update();
    }

    @Override
    public CompoundTag getNbtData(Block block) {
        TileEntity tileEntity = ((CraftBlockState) block.getState()).getTileEntity();
        if (tileEntity == null) {
            return null;
        }
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        tileEntity.b(new NBTTagCompound());
        return CompoundTag_v1_8_R3.fromNMSTag(nbtTagCompound);
    }

    @Override
    public void setNbtData(Block block, CompoundTag compoundTag) {
        TileEntity tileEntity = ((CraftBlockState) block.getState()).getTileEntity();
        if (tileEntity == null) {
            return;
        }
        tileEntity.a(((CompoundTag_v1_8_R3) compoundTag).toNMSTag());
        tileEntity.update();
    }

    @Override
    public BlockData getBlockData(short id, byte data) {
        return new BlockData_v1_8_R3(id, data);
    }

    @Override
    public BlockData getBlockData(Block block) {
        return new BlockData_v1_8_R3(block);
    }

    @Override
    public BlockData getBlockData(String compressedString) {
        return BlockData_v1_8_R3.fromCompressedString(compressedString);
    }

    @Override
    public boolean isSafeBlock(Material material) {
        // Quick util function to decide whether
        // A block is 'safe' (Can be spawned inside of) - air, tallgrass, etc.
        // Credit to Mythan for compiling the initial list
        switch (material) {
            case LEVER:
            case WOOD_BUTTON:
            case STONE_BUTTON:
            case REDSTONE_WIRE:
            case SAPLING:
            case SIGN_POST:
            case WALL_SIGN:
            case SNOW:
            case TORCH:
            case DETECTOR_RAIL:
            case ACTIVATOR_RAIL:
            case RAILS:
            case POWERED_RAIL:
            case NETHER_WARTS:
            case NETHER_STALK:
            case VINE:
            case SUGAR_CANE_BLOCK:
            case CROPS:
            case LONG_GRASS:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case DEAD_BUSH:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case AIR:
            case YELLOW_FLOWER:
            case RED_ROSE:
                return true;
            default:
                return false;
        }
    }
}
