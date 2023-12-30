package me.mrletsplay.archiveserver.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import me.mrletsplay.archiveserver.ArchiveServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	// public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runGenerationTask(Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks)
	@Inject(method = "runGenerationTask(Ljava/util/concurrent/Executor;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/server/world/ServerLightingProvider;Ljava/util/function/Function;Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
	public void runGenerationTask(Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter, List<Chunk> chunks, CallbackInfoReturnable<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> callback) {
		if(!ArchiveServer.isArchived(world.getRegistryKey().getValue())) return;

		Chunk theChunk = chunks.get(chunks.size() / 2);

		if((Object) this == ChunkStatus.FULL || (Object) this == ChunkStatus.LIGHT) return; // Conversion to full chunk

		if(theChunk instanceof ProtoChunk proto) {
			int currentIndex = ((ChunkStatus) (Object) this).getIndex();
			ChunkStatus newStatus;
			if(currentIndex == ChunkStatus.NOISE.getIndex()) {
				// The chunk needs noise generation, probably an edge chunk. Fill it with barriers
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				Heightmap heightmap = theChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
				Heightmap heightmap2 = theChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
				Block b = Blocks.BARRIER;
				for (int i = 0; i < theChunk.getHeight(); ++i) {
					BlockState blockState = b.getDefaultState();
					if (blockState == null)
						continue;
					int j = theChunk.getBottomY() + i;
					for (int k = 0; k < 16; ++k) {
						for (int l = 0; l < 16; ++l) {
							theChunk.setBlockState(mutable.set(k, j, l), blockState, false);
							heightmap.trackUpdate(k, j, l, blockState);
							heightmap2.trackUpdate(k, j, l, blockState);
						}
					}
				}
				newStatus = ChunkStatus.LIGHT.getPrevious(); // Chunk needs to be lit
			}else {
				// Chunk is either partially generated or completely empty. Mark it as fully generated
				newStatus = ChunkStatus.FULL;
			}

			proto.setStatus(newStatus);
			callback.setReturnValue(CompletableFuture.completedFuture(Either.left(theChunk)));
		}else {
			callback.setReturnValue(CompletableFuture.completedFuture(Either.left(theChunk)));
		}

//		callback.setReturnValue(ChunkHolder.UNLOADED_CHUNK_FUTURE);
//		WorldChunk
//		callback.setReturnValue(ChunkStatus.EMPTY.runGenerationTask(executor, world, generator, structureTemplateManager, lightingProvider, fullChunkConverter, chunks));
//		callback.setReturnValue(CompletableFuture.completedFuture(Either.left(chunks.get(chunks.size() / 2))));
//		callback.setReturnValue(CompletableFuture.completedFuture(Either.left(new WorldChunk(world, new ChunkPos(0, 0)))));
	}

}
