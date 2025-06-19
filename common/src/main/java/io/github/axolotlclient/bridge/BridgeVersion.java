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

package io.github.axolotlclient.bridge;

import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

import lombok.Getter;

@Getter
public enum BridgeVersion {
	V1_8("1.8.9"),
	V1_16_COMBAT("1.16_combat-6"),
	V1_20("1.20"),
	V1_21("1.21"),
	V1_21_4("1.21.4");

	private final String name;

	BridgeVersion(String name) {
		this.name = name;
	}

	public static BridgeVersion version() {
		return PlatformImplInternal.getBridgeApiVersion();
	}

	public boolean isCurrent() {
		return this == version();
	}
}
