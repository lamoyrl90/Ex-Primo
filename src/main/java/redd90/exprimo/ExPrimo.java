package redd90.exprimo;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import redd90.exprimo.event.ClientEventHandler;
import redd90.exprimo.event.ModEventHandler;
import redd90.exprimo.registry.Essentias;
import redd90.exprimo.registry.ModBlocks;
import redd90.exprimo.registry.ModItems;
import redd90.exprimo.registry.ModParticles;
import redd90.exprimo.registry.ModRegistries;
import redd90.exprimo.registry.ModTiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExPrimo.MODID)
public class ExPrimo
{
	public static final String MODID = "exprimo";
    public static final Logger LOGGER = LogManager.getLogger();
     
    public ExPrimo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(ModRegistries::createRegistries);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(ModParticles::onRegisterParticleFactories);
        modEventBus.addGenericListener(ParticleType.class, ModParticles::onRegisterParticleTypes);
        modEventBus.addListener(ClientEventHandler::onRegisterItemColors);
        
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(ModEventHandler::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onDebugRender);
        
        Essentias.ESSENTIAS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTiles.TILES.register(modEventBus);
    }
    
    public void commonSetup(final FMLCommonSetupEvent event) {
    	MinecraftForge.EVENT_BUS.addGenericListener(Chunk.class, ModEventHandler::onAttachChunkCaps);
    	MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ModEventHandler::onAttachEntityCaps);
    	MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, ModEventHandler::onAttachItemCaps);
    	MinecraftForge.EVENT_BUS.addListener(ModEventHandler::onChunkLoad);
    }
    
    public void clientSetup(final FMLClientSetupEvent event) {
    	ModTiles.registerAllTERs();
    }
    
    public void serverStarting(final FMLServerStartingEvent event) {
    	MinecraftForge.EVENT_BUS.addListener(ModEventHandler::onServerWorldTick);
    }

}
