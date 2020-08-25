package redd90.exprimo.registry;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.client.render.tile.IModTER;
import redd90.exprimo.client.render.tile.PedestalTER;
import redd90.exprimo.tile.StonePedestalTile;

public class ModTiles {

	private static Queue<Runnable> clientWorkQueue = new LinkedList<>();
	public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExPrimo.MODID);
	
	public static final RegistryObject<TileEntityType<StonePedestalTile>> STONE_PEDESTAL = registerType(ModBlocks.STONE_PEDESTAL, new PedestalTER<StonePedestalTile>(), () -> new StonePedestalTile());
	
	private static <E extends TileEntity, B extends Block> RegistryObject<TileEntityType<E>> registerType(RegistryObject<B> blockentry, IModTER<E> renderer, Supplier<E> factory) {
		RegistryObject<TileEntityType<E>> regobj = TILES.register(blockentry.getId().getPath(), () -> TileEntityType.Builder.create(factory, blockentry.get()).build(null));
		clientWorkQueue.add(() -> registerTER(renderer, regobj.get()));
		return regobj;
	}
	
	public static void registerAllTERs() {
		while (!clientWorkQueue.isEmpty()) {
			Runnable msg = clientWorkQueue.poll();
			if (msg != null) {
				msg.run();
			}
		}
	}
	
	public static <E extends TileEntity> void registerTER(IModTER<E> renderer, TileEntityType<E> type) {
		renderer.register(type);
	}
	
}
