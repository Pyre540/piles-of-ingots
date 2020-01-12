package pyre.pilesofingots.setup;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import pyre.pilesofingots.block.PileOfIngotsBlock;
import pyre.pilesofingots.block.PileOfIngotsTileEntity;

import static pyre.pilesofingots.PilesOfIngots.MODID;

public class ModSetup {

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<PileOfIngotsBlock> PILE_OF_INGOTS = BLOCKS.register("pile_of_ingots", PileOfIngotsBlock::new);
    public static final RegistryObject<TileEntityType<?>> TYPE_PILE_OF_INGOTS = TILE_ENTITIES.register("pile_of_ingots", () -> TileEntityType.Builder.create(PileOfIngotsTileEntity::new, PILE_OF_INGOTS.get()).build(null));

    public void init() {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
    }
}
