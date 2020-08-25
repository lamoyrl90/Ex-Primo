package redd90.exprimo.essentia.flow;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;

public class TileEssentiaFlowManager {
	public static void onTileTick(TileEntity te) {
		if (!te.getWorld().isRemote()) {
			ServerWorld world = (ServerWorld) te.getWorld();
			TileEssentiaFlowProvider tileflow = new TileEssentiaFlowProvider(world, te);
			
//			if (world.chunkExists(te.getPos().getX(), te.getPos().getZ())) {
				ChunkEssentiaFlowProvider chunkflow = new ChunkEssentiaFlowProvider(te, tileflow.sourcecontainers);
				ChunkEssentiaFlowManager.scheduleTileTick(chunkflow);
//			}
			
			tileflow.flow();
		}
	}
}
