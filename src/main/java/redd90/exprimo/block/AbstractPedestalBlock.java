package redd90.exprimo.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
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
            	Inventory itemhandler = ((AbstractPedestalTile)tileEntity).getItemHandler();
            	ItemStack handstack = player.getHeldItemMainhand();
            	if (itemhandler.getStackInSlot(0).isEmpty() && !handstack.isEmpty()) {
            		ItemStack stacktoplace = new ItemStack(handstack.getItem(), 1);
            		itemhandler.setInventorySlotContents(0, stacktoplace);
            		if (!player.isCreative())
            			handstack.shrink(1);
            	} else if (!itemhandler.getStackInSlot(0).isEmpty()) {
            		ItemStack stacktograb = new ItemStack(itemhandler.getStackInSlot(0).getItem(), 1);
            		itemhandler.decrStackSize(0, 1);
            		player.addItemStackToInventory(stacktograb);
            	}
            } else {
                throw new IllegalStateException("Block missing tile at pos" + pos.toString());
            }
        }
        return ActionResultType.SUCCESS;
	}
}
