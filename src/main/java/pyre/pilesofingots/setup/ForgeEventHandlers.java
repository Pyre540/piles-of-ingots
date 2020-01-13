package pyre.pilesofingots.setup;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import pyre.pilesofingots.block.PileOfIngotsBlock;
import pyre.pilesofingots.block.PileOfIngotsTileEntity;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockClickedWithIngot(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();
        ItemStack heldStack = player.getHeldItem(hand);
        if (hand == Hand.OFF_HAND || heldStack.isEmpty()) {
            return;
        }
        Tag<Item> itemTag = ItemTags.getCollection().get(new ResourceLocation("forge", "ingots"));
        if (itemTag == null || !itemTag.contains(heldStack.getItem())) {
            return;
        }

        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (!player.isSneaking()) {
            if (block instanceof PileOfIngotsBlock && !((PileOfIngotsBlock) block).isPileFull(world, pos)) {
                return;
            }
            if (canPlacePile(event, pos)) {
                createPile(world, player, hand, heldStack, pos);
                return;
            }
        }

        if (event.getFace() == null) {
            return;
        }
        pos = pos.offset(event.getFace());
        if (canPlacePile(event, pos)) {
            createPile(world, player, hand, heldStack, pos);
            return;
        }
        BlockState state = world.getBlockState(pos);
        block = state.getBlock();
        if (!player.isSneaking() && block instanceof PileOfIngotsBlock && !((PileOfIngotsBlock) block).isPileFull(world, pos)) {
            state.onBlockActivated(world, player, hand,
                    new BlockRayTraceResult(new Vec3d(0.5, 0.5, 0.5), event.getFace(), pos, false));
        }
    }

    private void createPile(World world, PlayerEntity player, Hand hand, ItemStack heldStack, BlockPos pos) {
        if (!world.isRemote) {
            BlockState stateForPlacement = ModSetup.PILE_OF_INGOTS.get().getDefaultState();
            world.setBlockState(pos, stateForPlacement);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PileOfIngotsTileEntity) {
                ItemStack heldStackCopy = heldStack.copy();
                heldStackCopy.setCount(1);
                ((PileOfIngotsTileEntity) te).setIngot(heldStackCopy);
                ((PileOfIngotsTileEntity) te).setIngotCount(1);
            }
            SoundType soundType = stateForPlacement.getSoundType(world, pos, player);
            world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F,
                    soundType.getPitch() * 0.8F);
            heldStack.shrink(1);
            player.setHeldItem(hand, heldStack);
        }
        player.swingArm(hand);
    }

    private boolean canPlacePile(PlayerInteractEvent.RightClickBlock event, BlockPos pos) {
        World world = event.getWorld();
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof PileOfIngotsBlock) {
            return false;
        }
        return world.getBlockState(pos).isReplaceable(new CustomBlockItemUseContext(event));
    }

    private class CustomBlockItemUseContext extends BlockItemUseContext {

        public CustomBlockItemUseContext(PlayerInteractEvent.RightClickBlock event) {
            super(event.getWorld(), event.getPlayer(), event.getHand(),
                    new ItemStack(event.getWorld().getBlockState(event.getPos()).getBlock().asItem()),
                    new BlockRayTraceResult(new Vec3d(0.5, 0.5, 0.5), event.getFace(), event.getPos(), false));
        }
    }
}
