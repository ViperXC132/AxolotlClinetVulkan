/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */
package io.github.axolotlclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("UnstableApiUsage")
public final class CachedAPI<K, V> {
    public interface APIHandler<K, V> {
        CompletableFuture<V> makeRequest(K key);
    }

    private final LoadingCache<K, CompletableFuture<V>> cache = CacheBuilder
        .newBuilder()
        .maximumSize(100)
        .build(new CacheLoader<>() {
            @Override
            public CompletableFuture<V> load(K key) {
                return apiHandler.makeRequest(key);
            }
        });

    private final APIHandler<K, V> apiHandler;

    public CachedAPI(APIHandler<K, V> apiHandler) {
        this.apiHandler = apiHandler;
    }

    public void invalidate() {
        cache.invalidateAll();
    }

	public void invalidate(K key) {
		cache.invalidate(key);
	}

	// don't use this...
	@ApiStatus.Obsolete
    public V getBlocking(K value) {
        return cache.getUnchecked(value).join();
    }

    public CompletableFuture<V> getAsync(K value) {
        return cache.getUnchecked(value);
    }

	public Optional<V> getAsyncNow(K value) {
		final var res = getAsync(value);
		return Optional.ofNullable(res.getNow(null));
	}
}
