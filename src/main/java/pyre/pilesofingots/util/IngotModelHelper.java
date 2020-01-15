package pyre.pilesofingots.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class IngotModelHelper {

    private static Map<Item, Color> colorMap = new HashMap<>();
    private static Map<Item, TextureAtlasSprite> ingotTextures = new HashMap<>();

    public static Color getColorForIngot(Item ingot) {
        if (colorMap.containsKey(ingot)) {
            return colorMap.get(ingot);
        }
        TextureAtlasSprite texture = getIngotTexture(ingot);
        int rgba = texture.getPixelRGBA(0, 7, 7);
        Color color = new Color(rgba);
        colorMap.put(ingot, color);
        return color;
    }

    public static Color getAverageColor(Item ingot) {
        if (colorMap.containsKey(ingot)) {
            return colorMap.get(ingot);
        }

        TextureAtlasSprite texture = getIngotTexture(ingot);
        int counter = 0;
        float sumR = 0F;
        float sumG = 0F;
        float sumB = 0F;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int rgba = texture.getPixelRGBA(0, 6 + i, 6 + j);
                sumB += ( rgba >> 16 & 255 ) / 255.0F;
                sumG += ( rgba >> 8 & 255 ) / 255.0F;
                sumR += ( rgba & 255 ) / 255.0F;
                counter++;
            }
        }

        Color color = new Color(sumR / counter, sumG / counter, sumB / counter);
        colorMap.put(ingot, color);
        return color;
    }

    public static TextureAtlasSprite getIngotTexture(Item ingot) {
        if (ingotTextures.containsKey(ingot)) {
            return ingotTextures.get(ingot);
        }
        TextureAtlasSprite texture =
                Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(ingot);
        ingotTextures.put(ingot, texture);
        return texture;
    }

    public static class Color {

        private int rgba;
        private float r;
        private float g;
        private float b;

        public Color(int rgba) {
            this.rgba = rgba;
            this.b = ( rgba >> 16 & 255 ) / 255.0F;
            this.g = ( rgba >> 8 & 255 ) / 255.0F;
            this.r = ( rgba & 255 ) / 255.0F;
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
