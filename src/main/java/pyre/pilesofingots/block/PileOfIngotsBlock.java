package pyre.pilesofingots.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class PileOfIngotsBlock extends Block {

    private final VoxelShape shape1Layer = VoxelShapes.create(0, 0, 0, 1, .125, 1);
    private final VoxelShape shape2Layers = VoxelShapes.create(0, 0, 0, 1, .25, 1);
    private final VoxelShape shape3Layers = VoxelShapes.create(0, 0, 0, 1, .375, 1);
    private final VoxelShape shape4Layers = VoxelShapes.create(0, 0, 0, 1, .5, 1);
    private final VoxelShape shape5Layers = VoxelShapes.create(0, 0, 0, 1, .625, 1);
    private final VoxelShape shape6Layers = VoxelShapes.create(0, 0, 0, 1, .75, 1);
    private final VoxelShape shape7Layers = VoxelShapes.create(0, 0, 0, 1, .8755, 1);
    private final VoxelShape shape8Layers = VoxelShapes.create(0, 0, 0, 1, 1, 1);
    private final VoxelShape[] shapes = {shape1Layer, shape2Layers, shape3Layers, shape4Layers, shape5Layers, shape6Layers, shape7Layers, shape8Layers};

    public PileOfIngotsBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(3.0F, 10.0F)
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE));
    }

    public boolean isPileFull(World world, BlockPos pos) {
        PileOfIngotsTileEntity te = getTE(world, pos);
        if (te != null) {
            return isPileFull(te);
        }
        return false;
    }

    public boolean isPileFull(PileOfIngotsTileEntity te) {
        return te.getIngotCount() == 64;
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public boolean isVariableOpacity() {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        PileOfIngotsTileEntity te = getTE(reader, pos);
        if (te != null) {
            int ingotCount = te.getIngotCount();
            int index = ingotCount / 8;
            if (ingotCount % 8 == 0) {
                index--;
            }
            return shapes[index];
        }
        return shape1Layer;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PileOfIngotsTileEntity();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn == Hand.OFF_HAND) {
            return false;
        }
        PileOfIngotsTileEntity te = getTE(worldIn, pos);
        if (player.isSneaking()) {
            return removeIngot(worldIn, pos, player, te);
        }
        return addIngot(state, worldIn, pos, player, handIn, te);
    }

    private boolean removeIngot(World worldIn, BlockPos pos, PlayerEntity player, PileOfIngotsTileEntity te) {
        if (te == null) {
            return false;
        }
        int ingotCount = te.getIngotCount();
        if (ingotCount > 0) {
            if (!worldIn.isRemote) {
                te.setIngotCount(te.getIngotCount() - 1);
                ItemHandlerHelper.giveItemToPlayer(player, te.getIngot().copy());
                if (te.getIngotCount() == 0) {
                    worldIn.removeBlock(pos, false);
                }
            }
            return true;
        }
        return false;
    }

    private boolean addIngot(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, PileOfIngotsTileEntity te) {
        if (te == null || isPileFull(te)) {
            return false;
        }
        ItemStack heldStack = player.getHeldItem(handIn);
        Tag<Item> itemTag = ItemTags.getCollection().get(new ResourceLocation("forge", "ingots"));
        if (!heldStack.isEmpty() && itemTag != null && itemTag.contains(heldStack.getItem())) {
            boolean sameIngotType = te.getIngot().getItem().getTags().stream()
                    .anyMatch(r -> r.getPath().startsWith("ingots/") && heldStack.getItem().getTags().contains(r));
            if (sameIngotType) {
                if (!worldIn.isRemote) {
                    te.setIngotCount(te.getIngotCount() + 1);
                    SoundType soundType = getSoundType(state, worldIn, pos, player);
                    worldIn.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F,
                            soundType.getPitch() * 0.8F);
                    if (!player.isCreative()) {
                        heldStack.shrink(1);
                        player.setHeldItem(handIn, heldStack);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private PileOfIngotsTileEntity getTE(IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PileOfIngotsTileEntity) {
            return (PileOfIngotsTileEntity) te;
        }
        return null;
    }
}
