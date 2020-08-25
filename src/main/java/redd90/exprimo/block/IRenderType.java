package redd90.exprimo.block;

import net.minecraft.client.renderer.RenderType;

public interface IRenderType {
	default RenderType getRenderType() {
		return RenderType.getSolid();
	}
}
