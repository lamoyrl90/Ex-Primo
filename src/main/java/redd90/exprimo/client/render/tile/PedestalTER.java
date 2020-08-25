package redd90.exprimo.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import redd90.exprimo.tile.AbstractPedestalTile;

public class PedestalTER <E extends AbstractPedestalTile> extends TileEntityRenderer<E> {

	public PedestalTER(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(E te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer,
			int light, int overlay) {
		ms.push();
		
		ms.translate(0.5, 1.0, 0.5);
		ItemRenderer itemrenderer = Minecraft.getInstance().getItemRenderer();
		ItemStack stack = te.getInventory().getStackInSlot(0);
		IBakedModel model = itemrenderer.getItemModelWithOverrides(stack, te.getWorld(), null);
		double tick = System.currentTimeMillis() / 800.0D;
        ms.translate(0.0D, Math.sin(tick % (2 * Math.PI)) * 0.065D, 0.0D);
        ms.rotate(Vector3f.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
		itemrenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND, true, ms, buffer, light, overlay, model);
		
		ms.pop();
	}

}
