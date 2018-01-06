package com.thedroide.sc18.cache;

import java.util.function.Supplier;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.utils.LinkedCacheMap;
import com.thedroide.sc18.utils.MapTable;
import com.thedroide.sc18.utils.Table;

/**
 * A cache that saves child game state nodes to avoid unnecessary object creation.
 * It has a maximum size and will discard the "oldest" (first inserted) mappings when
 * this limit is reached (or possibly earlier) and new mappings are inserted.
 */
public class GameChildCache {
	private final Table<HUIGameState, HUIMove, HUIGameState> cache;
	
	/**
	 * Creates a new game cache using the specified size.
	 * 
	 * @param size - The maximum cache size
	 */
	public GameChildCache(int size) {
		int sqrtSize = (int) Math.sqrt(size);
		cache = new MapTable<>(() -> new LinkedCacheMap<>(sqrtSize), () -> new LinkedCacheMap<>(sqrtSize));
	}
	
	/**
	 * Caches a child game state. The game before the move and the move itself
	 * are used as keys to map the child state (after the move). If the cache size
	 * approaches the maximum size, it may delete the oldest keys.
	 * 
	 * @param gameBeforeMove  - The game state (before the move)
	 * @param move - The move that would lead to the child state
	 * @param gameAfterMove - The child game state (after the move)
	 */
	public void storeChild(HUIGameState gameBeforeMove, HUIMove move, HUIGameState gameAfterMove) {
		synchronized (cache) {
			cache.put(gameBeforeMove, move, gameAfterMove);
		}
	}
	
	public HUIGameState getOrStoreChild(HUIGameState gameBeforeMove, HUIMove move, Supplier<HUIGameState> gameAfterMove) {
		synchronized (cache) {
			HUIGameState child = cache.get(gameBeforeMove, move);
			
			if (child == null) {
				child = gameAfterMove.get();
				cache.put(gameBeforeMove, move, child);
			}
			
			return child;
		}
	}
	
	/**
	 * Retrieves a previously associated child node mapping given the game state
	 * and the move. <b>The returned object should only be used in a <u>read-only</u>
	 * matter or it might result in unexpected bugs elsewhere.</b>
	 * 
	 * @param gameBeforeMove - The game state (before the move)
	 * @param move - The move that would lead to the child state
	 * @return The child game state (after the move)
	 */
	public HUIGameState getChild(HUIGameState gameBeforeMove, HUIMove move) {
		synchronized (cache) {
			return cache.get(gameBeforeMove, move);
		}
	}
}
