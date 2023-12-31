package me.mrletsplay.archiveserver.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mrletsplay.archiveserver.ArchiveServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

@Mixin(SerializingRegionBasedStorage.class)
public class POISaveMixin {

	// public void saveChunk(ChunkPos pos)
	@Inject(method = "saveChunk(Lnet/minecraft/util/math/ChunkPos;)V", at = @At("HEAD"), cancellable = true)
	private void saveChunk(ChunkPos pos, CallbackInfo callback) {
		Object me = this;
		if(!(me instanceof PointOfInterestStorage)) return;

		World world = (World) ((SerializingRegionBasedStorageAccessor) this).getWorld();
		if(!ArchiveServer.isArchived(world.getRegistryKey().getValue())) return;

		callback.cancel();
	}

}
