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

package io.github.axolotlclient.bridge.mixin.world;

import java.util.Collections;
import java.util.List;

import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.math.Vec3;
import io.github.axolotlclient.bridge.world.AxoWorld;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements AxoWorld, WorldAccess {
	@Shadow
	public abstract long getTimeOfDay();

	@Override
	public long br$getTimeOfDay() {
		return getTimeOfDay();
	}

	@Override
	public List<? extends AxoPlayer> br$getPlayers() {
		return Collections.unmodifiableList(getPlayers());
	}

	@Override
	public String br$getBiomeName(Vec3 pos) {
		var biome = getRegistryManager().get(Registry.BIOME_KEY).getId(getBiome(new BlockPos(pos.x(), pos.y(), pos.z())));
		if (biome == null) {
			return I18n.translate("coordshud.unknown_biome");
		}
		String path = biome.getPath();
		if (!biome.getNamespace().equals("minecraft")) {
			String namespace = biome.getNamespace();
			path += " (" + Character.toTitleCase(namespace.charAt(0)) + namespace.substring(1) + ")";
		}
		final String str = path.replace("_", " ");
		if (str.isEmpty()) {
			return str;
		}

		final int[] codepoints = str.codePoints().toArray();
		boolean capitalizeNext = true;
		for (int i = 0; i < codepoints.length; i++) {
			final int ch = codepoints[i];
			if (Character.isWhitespace(ch)) {
				capitalizeNext = true;
			} else if (capitalizeNext) {
				codepoints[i] = Character.toTitleCase(ch);
				capitalizeNext = false;
			}
		}
		return new String(codepoints, 0, codepoints.length);
	}
}
