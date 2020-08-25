package redd90.exprimo.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import redd90.exprimo.tile.AbstractPedestalTile;

public class PedestalTER <E extends AbstractPedestalTile> extends TileEntityRenderer<E> implements IModTER<E> {

	public PedestalTER() {
		super(TileEntityRendererDispatcher.instance);
	}

	@Override
	public void render(E te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer,
			int light, int overlay) {
		ms.push();
		
		ms.translate(0.5, 0.875, 0.5);
		ItemStack stack = te.getItemHandler().getStackInSlot(0);
		Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffer);
		
		ms.pop();
	}

	public void register(TileEntityType<E> type) {
		TileEntityRendererDispatcher.instance.setSpecialRendererInternal(type, new PedestalTER<E>());
	}

}
