package redd90.exprimo.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import redd90.exprimo.item.inventory.ModItemStackHandler;
import redd90.exprimo.tile.AbstractPedestalTile;

public abstract class AbstractPedestalBlock extends ModBlock {

	public AbstractPedestalBlock(Properties properties) {
		super(properties);
	}

	
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AbstractPedestalTile) {
            	ModItemStackHandler itemhandler = ((AbstractPedestalTile)tileEntity).getInventory();
            	ItemStack handstack = player.getHeldItemMainhand();
            	if (itemhandler.getStackInSlot(0).isEmpty() && !handstack.isEmpty()) {
            		ItemStack stacktoplace = new ItemStack(handstack.getItem(), 1);
            		itemhandler.insertItem(0, stacktoplace, false);
            		if (!player.isCreative())
            			handstack.shrink(1);
            	} else if (!itemhandler.getStackInSlot(0).isEmpty()) {
            		ItemStack stacktograb = new ItemStack(itemhandler.getStackInSlot(0).getItem(), 1);
            		itemhandler.extractItem(0, 1, false);
            		int playerslot = player.inventory.getFirstEmptyStack();
            		if (playerslot == -1) {
            			ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stacktograb);
            			world.addEntity(itementity);
            		} else {
            			player.addItemStackToInventory(stacktograb);
            		}
            	}
            } else {
                throw new IllegalStateException("Block missing tile at pos" + pos.toString());
            }
        }
        return ActionResultType.SUCCESS;
	}
}
