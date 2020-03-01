package pyre.pilesofingots.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import pyre.pilesofingots.block.PileOfIngotsBlock;
import pyre.pilesofingots.block.PileOfIngotsTileEntity;
import pyre.pilesofingots.util.IngotModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PileOfIngotsBakedModel implements IDynamicBakedModel {

    private UVs uvTop = new UVs(uv(4, 4), uv(8, 4), uv(4, 12), uv(8, 12));
    private UVs uvBottom = new UVs(uv(12, 4), uv(16, 4), uv(12, 12), uv(16, 12));
    private UVs uvSideLeft = new UVs(uv(0, 3), uv(4, 4), uv(0, 13), uv(4, 12));
    private UVs uvSideRight = new UVs(uv(8, 4), uv(12, 3), uv(8, 12), uv(12, 13));
    private UVs uvSideTop = new UVs(uv(3, 0), uv(9, 0), uv(4, 4), uv(8, 4));
    private UVs uvSideBottom = new UVs(uv(4, 12), uv(8, 12), uv(3, 16), uv(9, 16));

    private UVs uvAltTop = new UVs(uv(4, 4), uv(12, 4), uv(4, 8), uv(12, 8));
    private UVs uvAltBottom = new UVs(uv(4, 12), uv(12, 12), uv(4, 16), uv(12, 16));
    private UVs uvAltSideLeft = new UVs(uv(0, 3), uv(4, 4), uv(0, 9), uv(4, 8));
    private UVs uvAltSideRight = new UVs(uv(12, 4), uv(16, 3), uv(12, 8), uv(16, 9));
    private UVs uvAltSideTop = new UVs(uv(3, 0), uv(13, 0), uv(4, 4), uv(12, 4));
    private UVs uvAltSideBottom = new UVs(uv(4, 8), uv(12, 8), uv(3, 12), uv(13, 12));

    private final VertexFormat format;

    public PileOfIngotsBakedModel(VertexFormat format) {
        this.format = format;
    }

    private TextureAtlasSprite getTexture(ItemStack item, boolean alternative) {
        if (item.getItem().getTags().stream().anyMatch(i -> i.getPath().contains("ingots") && i.getPath().endsWith("brick"))) {
            return IngotModelHelper.getBrickTexture(item.getItem(), alternative);
        }
        return IngotModelHelper.getIngotTexture(item.getItem(), alternative);
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
                                 Vec2f uv1, Vec2f uv2, Vec2f uv3, Vec2f uv4, IngotModelHelper.Color color) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.x, v1.y, v1.z, uv1.x, uv1.y, sprite, color.getR(), color.getG(), color.getB()); //0 0
        putVertex(builder, normal, v2.x, v2.y, v2.z, uv2.x, uv2.y, sprite, color.getR(), color.getG(), color.getB()); // 0 16
        putVertex(builder, normal, v3.x, v3.y, v3.z, uv3.x, uv3.y, sprite, color.getR(), color.getG(), color.getB());//16 16
        putVertex(builder, normal, v4.x, v4.y, v4.z, uv4.x, uv4.y, sprite, color.getR(), color.getG(), color.getB());//16 0
        return builder.build();
    }

    private Vec3d v(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    private Vec2f uv(float u, float v) {
        return new Vec2f(u, v);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        ItemStack ingot = extraData.getData(PileOfIngotsTileEntity.INGOT);
        Integer ingotCount = extraData.getData(PileOfIngotsTileEntity.INGOT_COUNT);
        if (side != null || ingot == null || state == null || ingotCount == null || ingotCount == 0) {
            return Collections.emptyList();
        }
        Direction direction = state.get(PileOfIngotsBlock.FACING);

        List<BakedQuad> quads = new ArrayList<>();
        for (int n = 0; n < ingotCount; n++) {
            float offsetX = (n % 4) * 0.25F;
            float offsetY = (n / 8) * 0.125F;
            float offsetZ = (n % 8) < 4 ? 0 : 0.5F;
            boolean rotated = (n / 8) % 2 != 0;
            quads.addAll(createIngotQuads(ingot, offsetX, offsetY, offsetZ, rotated, direction));
        }
        return quads;
    }

    private List<BakedQuad> createIngotQuads(ItemStack ingot, double offsetX, double offsetY, double offsetZ, boolean rotated, Direction direction) {
        IngotModelHelper.Color color = IngotModelHelper.getColor(ingot.getItem());
        TextureAtlasSprite texture = getTexture(ingot, rotated);
        List<BakedQuad> quads = new ArrayList<>();
        Vec3d a1, a2, b1, b2, c1, c2, d1, d2;
        UVs t, b, sl, sr, st, sb;
        double r = getRotation(direction);

        if (rotated) {
            a1 = v(0.015625 + offsetZ, 0 + offsetY, 0.234375 + offsetX);
            b1 = v(0.484375 + offsetZ, 0 + offsetY, 0.234375 + offsetX);
            c1 = v(0.015625 + offsetZ, 0 + offsetY, 0.015625 + offsetX);
            d1 = v(0.484375 + offsetZ, 0 + offsetY, 0.015625 + offsetX);
            a2 = v(0.0625 + offsetZ, 0.125 + offsetY, 0.1875 + offsetX);
            b2 = v(0.4375 + offsetZ, 0.125 + offsetY, 0.1875 + offsetX);
            c2 = v(0.0625 + offsetZ, 0.125 + offsetY, 0.0625 + offsetX);
            d2 = v(0.4375 + offsetZ, 0.125 + offsetY, 0.0625 + offsetX);
            t = uvAltTop;
            b = uvAltBottom;
            sl = uvAltSideLeft;
            sr = uvAltSideRight;
            st = uvAltSideTop;
            sb = uvAltSideBottom;
        } else {
            a1 = v(0.015625 + offsetX, 0 + offsetY, 0.484375 + offsetZ);
            b1 = v(0.234375 + offsetX, 0 + offsetY, 0.484375 + offsetZ);
            c1 = v(0.015625 + offsetX, 0 + offsetY, 0.015625 + offsetZ);
            d1 = v(0.234375 + offsetX, 0 + offsetY, 0.015625 + offsetZ);
            a2 = v(0.0625 + offsetX, 0.125 + offsetY, 0.4375 + offsetZ);
            b2 = v(0.1875 + offsetX, 0.125 + offsetY, 0.4375 + offsetZ);
            c2 = v(0.0625 + offsetX, 0.125 + offsetY, 0.0625 + offsetZ);
            d2 = v(0.1875 + offsetX, 0.125 + offsetY, 0.0625 + offsetZ);
            t = uvTop;
            b = uvBottom;
            sl = uvSideLeft;
            sr = uvSideRight;
            st = uvSideTop;
            sb = uvSideBottom;
        }

        quads.add(createQuad(rotate(b1, r), rotate(a1, r), rotate(c1, r), rotate(d1, r), texture, b.bl, b.br, b.tr, b.tl, color)); //bottom
        quads.add(createQuad(rotate(a2, r), rotate(b2, r), rotate(d2, r), rotate(c2, r), texture, t.bl, t.br, t.tr, t.tl, color)); //top
        quads.add(createQuad(rotate(a1, r), rotate(a2, r), rotate(c2, r), rotate(c1, r), texture, sl.bl, sl.br, sl.tr, sl.tl, color)); //side left
        quads.add(createQuad(rotate(d1, r), rotate(d2, r), rotate(b2, r), rotate(b1, r), texture, sr.tr, sr.tl, sr.bl, sr.br, color)); //side right
        quads.add(createQuad(rotate(a2, r), rotate(a1, r), rotate(b1, r), rotate(b2, r), texture, sb.tl, sb.bl, sb.br, sb.tr, color)); //side bottom
        quads.add(createQuad(rotate(c1, r), rotate(c2, r), rotate(d2, r), rotate(d1, r), texture, st.tl, st.bl, st.br, st.tr, color)); //side top
        return quads;
    }

    private Vec3d rotate(Vec3d v, double angle) {
        double sin = Math.sin(Math.toRadians(angle));
        double cos = Math.cos(Math.toRadians(angle));

        //point (0.5, 0.5)
        double newX = (v.x - 0.5) * cos - (v.z - 0.5) * sin + 0.5;
        double newZ = (v.x - 0.5) * sin + (v.z - 0.5) * cos + 0.5;
        return new Vec3d(newX, v.y, newZ);
    }

    private double getRotation(Direction direction) {
        switch (direction) {
            case EAST:
                return 270;
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            default:
                return 180;
        }
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
        return IngotModelHelper.getParticleTexture(ingot);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    private class UVs {
        private Vec2f tl; //top left
        private Vec2f tr; //top right
        private Vec2f bl; //bottom left
        private Vec2f br; //bottom right

        public UVs(Vec2f tl, Vec2f tr, Vec2f bl, Vec2f br) {
            this.tl = tl;
            this.tr = tr;
            this.bl = bl;
            this.br = br;
        }
    }
}
