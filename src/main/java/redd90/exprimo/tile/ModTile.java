package redd90.exprimo.tile;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModTile extends TileEntity {

	public ModTile(TileEntityType<?> type) {
		super(type);
	}
	

    
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
		this.read(this.getBlockState(), packet.getNbtCompound());
	}

	@Override
	public final CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	public void markDirtyAndDispatch() {
		super.markDirty();
		dispatchToNearbyPlayers();
	}    
	
	public void dispatchToNearbyPlayers() {
        World world = getWorld();
        if (world == null)
            return;

        SUpdateTileEntityPacket packet = getUpdatePacket();
        if (packet == null)
            return;

        List<? extends PlayerEntity> players = world.getPlayers();
        BlockPos pos = getPos();
        for (Object player : players) {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity mPlayer = (ServerPlayerEntity) player;
                if (isPlayerNearby(mPlayer.getPosX(), mPlayer.getPosZ(), pos.getX() + 0.5, pos.getZ() + 0.5)) {
                    mPlayer.connection.sendPacket(packet);
                }
            }
        }
    }
    
    private static boolean isPlayerNearby(double x1, double z1, double x2, double z2) {
        return Math.hypot(x1 - x2, z1 - z2) < 64;
    }
}
