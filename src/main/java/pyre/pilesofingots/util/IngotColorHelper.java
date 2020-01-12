package pyre.pilesofingots.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class IngotColorHelper {

    private static Map<Item, Color> colorMap = new HashMap<>();

    public static Color getColorForIngot(Item ingot) {
        if (colorMap.containsKey(ingot)) {
            return colorMap.get(ingot);
        }
        TextureAtlasSprite texture =
                Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(ingot);
        int rgba = texture.getPixelRGBA(0, 7, 7);
        Color color = new Color(rgba);
        colorMap.put(ingot, color);
        return color;
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
