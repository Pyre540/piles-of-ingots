package pyre.pilesofingots.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import pyre.pilesofingots.block.PileOfIngotsTileEntity;
import pyre.pilesofingots.util.IngotModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static pyre.pilesofingots.PilesOfIngots.MODID;

public class PileOfIngotsBakedModel implements IDynamicBakedModel {

    private final String nameIngots = MODID + ":block/pile_of_ingots";
    private final String nameBricks = MODID + ":block/pile_of_bricks";

    private final VertexFormat format;
    private final TextureAtlasSprite textureIngots;
    private final TextureAtlasSprite textureBricks;

    public PileOfIngotsBakedModel(VertexFormat format) {
        this.format = format;
        textureIngots = Minecraft.getInstance().getTextureMap().getAtlasSprite(nameIngots);
        textureBricks = Minecraft.getInstance().getTextureMap().getAtlasSprite(nameBricks);
    }

    private TextureAtlasSprite getTexture(ItemStack item) {
        if (item.getItem().getTags().stream().anyMatch(i -> i.getPath().contains("ingots") && i.getPath().endsWith("brick"))) {
            return textureBricks;
        }
        return textureIngots;
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal,
                           double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float) x, (float) y, (float) z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, r, g, b, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite,
                                 IngotModelHelper.Color color) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, color.getR(), color.getG(), color.getB());
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, color.getR(), color.getG(), color.getB());
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, color.getR(), color.getG(), color.getB());
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, color.getR(), color.getG(), color.getB());
        return builder.build();
    }

    private static Vec3d v(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null) {
            return Collections.emptyList();
        }

        ItemStack ingot = extraData.getData(PileOfIngotsTileEntity.INGOT);
        if (ingot == null) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ArrayList<>();
        for (int n = 0; n < extraData.getData(PileOfIngotsTileEntity.INGOT_COUNT); n++) {
            float offsetX = (n % 4) * 0.25F;
            float offsetY = (n / 8) * 0.125F;
            float offsetZ = (n % 8) < 4 ? 0 : 0.5F;
            boolean rotated = (n / 8) % 2 != 0;
            quads.addAll(createIngotQuads(ingot, offsetX, offsetY, offsetZ, rotated));
        }
        return quads;
    }

    private List<BakedQuad> createIngotQuads(ItemStack ingot, double offsetX, double offsetY, double offsetZ, boolean rotated) {
        IngotModelHelper.Color color = IngotModelHelper.getAverageColor(ingot.getItem());
        TextureAtlasSprite texture = getTexture(ingot);
        List<BakedQuad> quads = new ArrayList<>();
        Vec3d a1, a2, b1, b2, c1, c2, d1, d2;

        if (rotated) {
            a1 = v(0.015625 + offsetZ, 0 + offsetY, 0.234375 + offsetX);
            b1 = v(0.484375 + offsetZ, 0 + offsetY, 0.234375 + offsetX);
            c1 = v(0.015625 + offsetZ, 0 + offsetY, 0.015625 + offsetX);
            d1 = v(0.484375 + offsetZ, 0 + offsetY, 0.015625 + offsetX);
            a2 = v(0.0625 + offsetZ, 0.125 + offsetY, 0.1875 + offsetX);
            b2 = v(0.4375 + offsetZ, 0.125 + offsetY, 0.1875 + offsetX);
            c2 = v(0.0625 + offsetZ, 0.125 + offsetY, 0.0625 + offsetX);
            d2 = v(0.4375 + offsetZ, 0.125 + offsetY, 0.0625 + offsetX);
        } else {
            a1 = v(0.015625 + offsetX, 0 + offsetY, 0.484375 + offsetZ);
            b1 = v(0.234375 + offsetX, 0 + offsetY, 0.484375 + offsetZ);
            c1 = v(0.015625 + offsetX, 0 + offsetY, 0.015625 + offsetZ);
            d1 = v(0.234375 + offsetX, 0 + offsetY, 0.015625 + offsetZ);
            a2 = v(0.0625 + offsetX, 0.125 + offsetY, 0.4375 + offsetZ);
            b2 = v(0.1875 + offsetX, 0.125 + offsetY, 0.4375 + offsetZ);
            c2 = v(0.0625 + offsetX, 0.125 + offsetY, 0.0625 + offsetZ);
            d2 = v(0.1875 + offsetX, 0.125 + offsetY, 0.0625 + offsetZ);
        }

        quads.add(createQuad(b1, a1, c1, d1, texture, color)); //bottom
        quads.add(createQuad(a2, b2, d2, c2, texture, color)); //top
        quads.add(createQuad(a1, a2, c2, c1, texture, color)); //long1
        quads.add(createQuad(d1, d2, b2, b1, texture, color)); //long2
        quads.add(createQuad(a2, a1, b1, b2, texture, color)); //short1
        quads.add(createQuad(c1, c2, d2, d1, texture, color)); //short2
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        ItemStack ingot = data.getData(PileOfIngotsTileEntity.INGOT);
        if (ingot == null) {
            return textureIngots;
        }
        return IngotModelHelper.getIngotTexture(ingot.getItem());
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
