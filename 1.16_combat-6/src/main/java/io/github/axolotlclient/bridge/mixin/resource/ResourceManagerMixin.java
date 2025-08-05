/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.bridge.mixin.resource;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.axolotlclient.bridge.resource.AxoResource;
import io.github.axolotlclient.bridge.resource.AxoResourceManager;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourceManager.class)
public interface ResourceManagerMixin extends AxoResourceManager {

	@Shadow
	Resource getResource(Identifier identifier) throws IOException;

	@Shadow
	Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate);

	@Override
	default Map<AxoIdentifier, AxoResource> br$listResources(String namespace, String prefix, Predicate<AxoIdentifier> filter) {
		// this cast is maybe not ideal
		return findResources(prefix, s -> filter.test(AxoIdentifier.of(s)))
			.stream().map(id -> {
				try {
					return getResource(id);
				} catch (IOException e) {
					return null;
				}
			}).filter(Objects::nonNull)
			.collect(Collectors.toMap(Resource::getId, r -> r));
	}

	@Override
	default AxoResource br$getResource(AxoIdentifier loc) throws IOException {
		return getResource((Identifier) loc);
	}
}
