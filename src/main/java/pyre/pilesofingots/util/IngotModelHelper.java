package pyre.pilesofingots.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

import static pyre.pilesofingots.PilesOfIngots.MODID;

public class IngotModelHelper {

    private static final String DEFAULT_INGOT = MODID + ":block/default_ingot";
    private static final String ALT_DEFAULT_INGOT = MODID + ":block/alt_default_ingot";
    private static final String DEFAULT_BRICK = MODID + ":block/default_brick";
    private static final String ALT_DEFAULT_BRICK = MODID + ":block/alt_default_brick";

    private static Map<Item, Color> colorMap = new HashMap<>();
    private static Map<Item, TextureAtlasSprite> textures = new HashMap<>();
    private static Map<Item, TextureAtlasSprite> altTextures = new HashMap<>();

    static {
        colorMap.put(Items.GOLD_INGOT, Color.WHITE);
        colorMap.put(Items.IRON_INGOT, Color.WHITE);

        AtlasTexture atlas = Minecraft.getInstance().getTextureMap();
        textures.put(Items.GOLD_INGOT, atlas.getAtlasSprite(MODID + ":block/gold_ingot"));
        textures.put(Items.IRON_INGOT, atlas.getAtlasSprite(MODID + ":block/iron_ingot"));

        altTextures.put(Items.GOLD_INGOT, atlas.getAtlasSprite(MODID + ":block/alt_gold_ingot"));
        altTextures.put(Items.IRON_INGOT, atlas.getAtlasSprite(MODID + ":block/alt_iron_ingot"));
    }

    public static Color getColor(Item ingot) {
        if (colorMap.containsKey(ingot)) {
            return colorMap.get(ingot);
        }
        TextureAtlasSprite texture =
                Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(ingot);
        int counter = 0;
        float sumR = 0F;
        float sumG = 0F;
        float sumB = 0F;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int rgba = texture.getPixelRGBA(0, 6 + i, 6 + j);
                sumB += (rgba >> 16 & 255) / 255.0F;
                sumG += (rgba >> 8 & 255) / 255.0F;
                sumR += (rgba & 255) / 255.0F;
                counter++;
            }
        }

        Color color = new Color(sumR / counter, sumG / counter, sumB / counter);
        colorMap.put(ingot, color);
        return color;
    }

    public static TextureAtlasSprite getIngotTexture(Item ingot, boolean alternative) {
        return getTexture(ingot, alternative, ALT_DEFAULT_INGOT, DEFAULT_INGOT);
    }

    public static TextureAtlasSprite getBrickTexture(Item brick, boolean alternative) {
        return getTexture(brick, alternative, ALT_DEFAULT_BRICK, DEFAULT_BRICK);
    }

    public static TextureAtlasSprite getParticleTexture(ItemStack stack) {
        if (stack == null) {
            return Minecraft.getInstance().getTextureMap().getAtlasSprite(DEFAULT_INGOT);
        }
        return textures.get(stack.getItem());
    }

    private static TextureAtlasSprite getTexture(Item item, boolean alternative, String altDefaultTexture,
                                                 String defaultTexture) {
        Map<Item, TextureAtlasSprite> tex = alternative ? altTextures : textures;
        if (tex.containsKey(item)) {
            return tex.get(item);
        }
        TextureAtlasSprite texture =
                Minecraft.getInstance().getTextureMap().getAtlasSprite(alternative ? altDefaultTexture : defaultTexture);
        tex.put(item, texture);
        return texture;
    }

    public static class Color {

        public static final Color WHITE = new Color(1, 1, 1);

        private int rgba;
        private float r;
        private float g;
        private float b;

        public Color(int rgba) {
            this.rgba = rgba;
            this.b = (rgba >> 16 & 255) / 255.0F;
            this.g = (rgba >> 8 & 255) / 255.0F;
            this.r = (rgba & 255) / 255.0F;
        }

        public Color(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getRgba() {
            return rgba;
        }

        public float getR() {
            return r;
        }

        public float getG() {
            return g;
        }

        public float getB() {
            return b;
        }
    }
}
