package redd90.exprimo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.registry.ModRegistries;

public class SetChunkEssentiaCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("setchunkessentia")
				.requires((source)-> {return source.hasPermissionLevel(2);})
				.then(Commands.argument("essentia", StringArgumentType.string())
				.then(Commands.argument("value", IntegerArgumentType.integer(0))
				.executes((context) -> {
					return setChunkEssentia(context.getSource(), 
							StringArgumentType.getString(context, "essentia"), 
							IntegerArgumentType.getInteger(context, "value"));
				})))
				);
	}
	
	private static int setChunkEssentia(CommandSource source, String essentia, int value) {
		ServerWorld world = source.getWorld();
		Chunk chunk = world.getChunkAt(source.getEntity().getPosition());
		EssentiaContainer container = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElseThrow(() -> new IllegalArgumentException("Invalid Essentia Container!"));
		Essentia e = ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, essentia));
		container.setStack(e, value);
		return 1;
	}
	
}
