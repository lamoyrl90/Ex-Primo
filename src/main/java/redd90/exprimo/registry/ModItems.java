package redd90.exprimo.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.item.EssentiaOrbItem;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExPrimo.MODID);
	
	public static final RegistryObject<EssentiaOrbItem> ESSENTIA_ORB_DULL = ITEMS.register("essentia_orb_dull", () -> new EssentiaOrbItem(1000, .025, 0.05));
	
	public static Item.Properties defaultProperties() {
		return new Item.Properties().group(MOD_GROUP);
	}
	
	public static final ItemGroup MOD_GROUP = new ItemGroup(ExPrimo.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.STONE_PEDESTAL.get());
        }
    };
}
