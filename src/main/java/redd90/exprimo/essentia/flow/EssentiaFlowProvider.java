package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.StackSet;
import redd90.exprimo.network.EssentiaPacket;
import redd90.exprimo.network.PacketHandler;
import redd90.exprimo.registry.ModRegistries;
import redd90.exprimo.util.ColorUtil;

public abstract class EssentiaFlowProvider {

	protected ICapabilityProvider holder;
	protected Set<EssentiaContainer> targetcontainers = new HashSet<>();
	protected Set<EssentiaContainer> sourcecontainers = new HashSet<>();
	protected Optional<TileEntity> te;
	
	public EssentiaFlowProvider(ICapabilityProvider holder) {
		this.holder = holder;
		this.te = Optional.empty();
	}

	protected abstract Set<EssentiaContainer> gatherSourceContainers();
	
	protected abstract Set<EssentiaContainer> gatherTargetContainers();
	
	public float[] flow(double factor) {
		Set<EssentiaFlow> flows = calculateFlows();
		StackSet virtualstackset = new StackSet();
		int count = flows.size();
		for(EssentiaFlow flow : flows) {
			int v = flow.getValue();
			EssentiaContainer source = flow.getSource();
			EssentiaContainer target = flow.getTarget();
			Essentia e = flow.getEssentia();
			if(v > 0) {
				int amount = Math.floorDiv((int) Math.floor(v*factor), count);
				source.transfer(e, target, amount);
				int u = virtualstackset.getAmount(e);
				virtualstackset.setAmount(e, u + amount);
				
				if(source.getHolder().get() instanceof ItemStack && getTile() != null) {
					PacketHandler.sendToAllTracking(new EssentiaPacket((ItemStack) source.getHolder().get(), getTile().getPos(), e.getKey(), source.getStack(e) - amount), getTile());
				}
				
				if(target.getHolder().get() instanceof ItemStack && getTile() != null) {
					PacketHandler.sendToAllTracking(new EssentiaPacket((ItemStack) target.getHolder().get(), getTile().getPos(), e.getKey(), target.getStack(e) + amount), getTile());
				}
				
			}
		}
		
		
		
		float[] flowcolor = ColorUtil.stacksetAverageColor(virtualstackset).getRGBColorComponents(null);
		
		return flowcolor;
		
	}
	
	protected Set<EssentiaFlow> calculateFlows() {
		Set<EssentiaFlow> flows = new HashSet<>();
		
		for(EssentiaContainer target : targetcontainers) {
			for(Essentia e : ModRegistries.ESSENTIAS) {
				for(EssentiaContainer source : sourcecontainers) {
					int diff = getPressureDiff(source, target, e);
					if (diff > 0) {
						flows.add(new EssentiaFlow(e, source, target, diff));
					}
				}
			}
		}
		
		return flows;
	}
	
	protected int getPressureDiff(EssentiaContainer source, EssentiaContainer target, Essentia essentia) {
		return (source.getInnerPressure(essentia) - target.getInnerPressure(essentia));
	}
	

	public TileEntity getTile() {
		if (te.isPresent())
			return te.get();
		return null;
	}
}
