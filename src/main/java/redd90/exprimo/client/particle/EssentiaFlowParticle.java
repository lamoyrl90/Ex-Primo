package redd90.exprimo.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

public class EssentiaFlowParticle extends SpriteTexturedParticle {

	private final IAnimatedSprite sprite;
	
	protected EssentiaFlowParticle(ClientWorld world, double x, double y,
			double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite sprite) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		this.sprite = sprite;

		this.motionX = xSpeed;
		this.motionY = ySpeed;
		this.motionZ = zSpeed;
		
		this.particleScale *= 0.75F;
		this.maxAge = 5;
		this.canCollide = false;
		this.selectSpriteWithAge(sprite);
	}
		
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
			this.selectSpriteWithAge(this.sprite);
			
			this.move(this.motionX, this.motionY, this.motionZ);
			posX = posX + motionX;
		}
	}
}
