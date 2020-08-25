package redd90.exprimo.registry;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.block.StonePedestalBlock;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExPrimo.MODID);

	public static final RegistryObject<StonePedestalBlock> STONE_PEDESTAL = createBlock("stone_pedestal", () -> new StonePedestalBlock());
	
	private static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<B> supplier) {
		RegistryObject<B> regObj = BLOCKS.register(name, supplier);
		ModItems.ITEMS.register(name, () -> new BlockItem(regObj.get(), ModItems.defaultProperties()));
		return regObj;
	}
}
