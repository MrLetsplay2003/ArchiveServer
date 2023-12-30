package me.mrletsplay.archiveserver.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mrletsplay.archiveserver.ArchiveServer;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ChunkSaveMixin {

	@Inject(method = "save(Lnet/minecraft/world/chunk/Chunk;)Z", at = @At("HEAD"), cancellable = true)
	private void save(Chunk chunk, CallbackInfoReturnable<Boolean> callback) {
		World world = ((ThreadedAnvilChunkStorageAccessor) this).getWorld();
		if(!ArchiveServer.isArchived(world.getRegistryKey().getValue())) return;

		if(!chunk.needsSaving()) {
			callback.setReturnValue(false);
			return;
		}

		chunk.setNeedsSaving(false);
		callback.setReturnValue(true);
	}

}