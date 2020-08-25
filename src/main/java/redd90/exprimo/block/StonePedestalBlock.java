package redd90.exprimo.block;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import redd90.exprimo.tile.StonePedestalTile;

public class StonePedestalBlock extends AbstractPedestalBlock {

	private static VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 12, 16);
	
	public StonePedestalBlock() {
		super(AbstractBlock.Properties.from(Blocks.STONE)
				.notSolid());
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new StonePedestalTile();
	}
	
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
}
