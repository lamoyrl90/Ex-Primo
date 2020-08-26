package redd90.exprimo.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import redd90.exprimo.item.inventory.ModItemStackHandler;
import redd90.exprimo.tile.AbstractPedestalTile;

public abstract class AbstractPedestalBlock extends ModBlock {

	public AbstractPedestalBlock(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newstate, boolean ismoving) {
		if (state.getBlock() != newstate.getBlock()) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof AbstractPedestalTile) {
				AbstractPedestalTile pedestal = (AbstractPedestalTile) tile;
				InventoryHelper.dropItems(world, pos, pedestal.getInventory().getStacks());
			}
		}
		
		super.onReplaced(state, world, pos, newstate, ismoving);
	}
	
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AbstractPedestalTile) {
            	ModItemStackHandler itemhandler = ((AbstractPedestalTile)tileEntity).getInventory();
            	ItemStack stackIn = itemhandler.getStackInSlot(0);
            	ItemStack handstack = player.getHeldItem(hand);
            	if (stackIn.isEmpty() && !handstack.isEmpty()) {
            		player.setHeldItem(hand, itemhandler.insertItem(0, handstack, false));
            		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            	} else if (!stackIn.isEmpty()) {
            		ItemStack stacktograb = itemhandler.extractItem(0, 1, false);
            		int playerslot = player.inventory.getFirstEmptyStack();
            		if (playerslot == -1) {
            			ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stacktograb);
            			world.addEntity(itementity);
            			itemhandler.setStackInSlot(0, ItemStack.EMPTY);
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
