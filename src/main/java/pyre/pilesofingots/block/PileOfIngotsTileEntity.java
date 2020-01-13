package pyre.pilesofingots.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static pyre.pilesofingots.setup.ModSetup.TYPE_PILE_OF_INGOTS;

public class PileOfIngotsTileEntity extends TileEntity {

    public static final ModelProperty<ItemStack> INGOT = new ModelProperty<>();
    public static final ModelProperty<Integer> INGOT_COUNT = new ModelProperty<>();

    private ItemStack ingot;
    private int ingotCount = 1;

    public PileOfIngotsTileEntity() {
        super(TYPE_PILE_OF_INGOTS.get());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        boolean newIngot = false;
        ItemStack oldIngot = ingot;
        CompoundNBT tag = pkt.getNbtCompound();
        if (tag.contains("ingot")) {
            ingot = ItemStack.read(tag.getCompound("ingot"));
            if (!Objects.equals(oldIngot, ingot)) {
                newIngot = true;
            }
        }
        int oldIngotCount = ingotCount;
        ingotCount = tag.getInt("ingotCount");
        if (newIngot || oldIngotCount != ingotCount) {
            ModelDataManager.requestModelDataRefresh(this);
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(),
                    Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        if (ingot != null) {
            tag.put("ingot", ingot.serializeNBT());
        }
        tag.putInt("ingotCount", ingotCount);
        return tag;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(INGOT, ingot)
                .withInitial(INGOT_COUNT, ingotCount)
                .build();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains("ingot")) {
            ingot = ItemStack.read(tag.getCompound("ingot"));
        }
        ingotCount = tag.getInt("ingotCount");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if (ingot != null) {
            tag.put("ingot", ingot.serializeNBT());
        }
        tag.putInt("ingotCount", ingotCount);
        return super.write(tag);
    }

    public ItemStack getIngot() {
        return ingot;
    }

    public void setIngot(ItemStack ingot) {
        this.ingot = ingot;
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(),
                Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public int getIngotCount() {
        return ingotCount;
    }

    public void setIngotCount(int ingotCount) {
        this.ingotCount = ingotCount;
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(),
                Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }
}
