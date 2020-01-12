package pyre.pilesofingots;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pyre.pilesofingots.setup.ModSetup;

@Mod(PilesOfIngots.MODID)
public class PilesOfIngots {
    public static final String MODID = "pilesofingots";

    public static final Logger LOGGER = LogManager.getLogger();

    public static ModSetup setup = new ModSetup();

    public PilesOfIngots() {
        ModSetup.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
    }

    /*@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
        }
    }*/
}
