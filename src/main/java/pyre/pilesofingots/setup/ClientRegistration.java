package pyre.pilesofingots.setup;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pyre.pilesofingots.PilesOfIngots;
import pyre.pilesofingots.model.PileOfIngotsBakedModel;

@Mod.EventBusSubscriber(modid = PilesOfIngots.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getBasePath().equals("textures")) {
            return;
        }
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/default_ingot"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/alt_default_ingot"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/default_brick"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/alt_default_brick"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/gold_ingot"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/iron_ingot"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/alt_gold_ingot"));
        event.addSprite(new ResourceLocation(PilesOfIngots.MODID, "block/alt_iron_ingot"));
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        event.getModelRegistry().put(new ModelResourceLocation(ModSetup.PILE_OF_INGOTS.get().getRegistryName(), "facing=north"),
                new PileOfIngotsBakedModel(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL));
        event.getModelRegistry().put(new ModelResourceLocation(ModSetup.PILE_OF_INGOTS.get().getRegistryName(), "facing=south"),
                new PileOfIngotsBakedModel(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL));
        event.getModelRegistry().put(new ModelResourceLocation(ModSetup.PILE_OF_INGOTS.get().getRegistryName(), "facing=west"),
                new PileOfIngotsBakedModel(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL));
        event.getModelRegistry().put(new ModelResourceLocation(ModSetup.PILE_OF_INGOTS.get().getRegistryName(), "facing=east"),
                new PileOfIngotsBakedModel(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL));
    }
}
