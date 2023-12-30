package me.mrletsplay.archiveserver.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.storage.EntityChunkDataAccess;

@Mixin(EntityChunkDataAccess.class)
public interface EntityChunkDataAccessAccessor {

	@Accessor("world")
	public ServerWorld getWorld();

}
