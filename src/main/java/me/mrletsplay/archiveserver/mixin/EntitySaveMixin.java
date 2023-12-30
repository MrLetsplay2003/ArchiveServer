package me.mrletsplay.archiveserver.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mrletsplay.archiveserver.ArchiveServer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.storage.ChunkDataList;
import net.minecraft.world.storage.EntityChunkDataAccess;

@Mixin(EntityChunkDataAccess.class)
public class EntitySaveMixin {

	// public void writeChunkData(ChunkDataList<Entity> dataList)
	@Inject(method = "writeChunkData(Lnet/minecraft/world/storage/ChunkDataList;)V", at = @At("HEAD"), cancellable = true)
	private void writeChunkData(ChunkDataList<Entity> dataList, CallbackInfo callback) {
		World world = ((EntityChunkDataAccessAccessor) this).getWorld();
		if(!ArchiveServer.isArchived(world.getRegistryKey().getValue())) return;

		callback.cancel();
	}

}
