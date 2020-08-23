package redd90.exprimo;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import redd90.exprimo.event.ClientEventHandler;
import redd90.exprimo.event.ModEventHandler;
import redd90.exprimo.network.ModPacketHandler;
import redd90.exprimo.registry.Essentias;
import redd90.exprimo.registry.ModRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExPrimo.MODID)
public class ExPrimo
{
	public static final String MODID = "exprimo";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ModPacketHandler packetHandler = new ModPacketHandler();
     
    public ExPrimo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(ModRegistries::createRegistries);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        Essentias.ESSENTIAS.register(modEventBus);
    }
    
    public void commonSetup(final FMLCommonSetupEvent event) {
    	MinecraftForge.EVENT_BUS.addGenericListener(Chunk.class, ModEventHandler::onAttachChunkCaps);
    	MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ModEventHandler::onAttachEntityCaps);
    	MinecraftForge.EVENT_BUS.addListener(ModEventHandler::onWorldTick);
    	//MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, ModEventHandler::onAttachItemCaps);
    	
    	packetHandler.init();
    }
    
    public void clientSetup(final FMLCommonSetupEvent event) {
    	MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onDebugRender);
    }

}
