package pyre.pilesofingots.setup;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import pyre.pilesofingots.block.PileOfIngotsTileEntity;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockClickedWithIngot(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        ItemStack heldStack = event.getPlayer().getHeldItem(event.getHand());
        if (world.isRemote || heldStack.isEmpty()) {
            return;
        }
        Tag<Item> itemTag = ItemTags.getCollection().get(new ResourceLocation("forge", "ingots"));
        if (itemTag != null && itemTag.contains(heldStack.getItem())) {
            BlockPos pos = event.getPos();
            boolean replaceable = world.getBlockState(pos).isReplaceable(new CustomBlockItemUseContext(event));
            if (!replaceable) {
                pos = pos.offset(event.getFace());
            }
            BlockState stateForPlacement = ModSetup.PILE_OF_INGOTS.get().getDefaultState();
            world.setBlockState(pos, stateForPlacement);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PileOfIngotsTileEntity) {
                ((PileOfIngotsTileEntity)te).setIngot(heldStack);
                ((PileOfIngotsTileEntity)te).setIngotCount(1);
            }
            SoundType soundType = stateForPlacement.getSoundType(world, pos, event.getPlayer());
            world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F,
                    soundType.getPitch() * 0.8F);
            heldStack.shrink(1);
            event.getPlayer().setHeldItem(event.getHand(), heldStack);
        }
    }

    private class CustomBlockItemUseContext extends BlockItemUseContext {

        public CustomBlockItemUseContext(PlayerInteractEvent.RightClickBlock event) {
            super(event.getWorld(), event.getPlayer(), event.getHand(),
                    new ItemStack(event.getWorld().getBlockState(event.getPos()).getBlock().asItem()),
                    new BlockRayTraceResult(new Vec3d(0.5, 0.5, 0.5), event.getFace(), event.getPos(), false));
        }
    }
}
