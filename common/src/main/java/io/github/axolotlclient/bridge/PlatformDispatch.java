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

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.modules.hud.HudManagerCommon;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Logic to dispatch to the platform implementation whenever it is not worth it to try and abstract things in a more
 * granular manner.
 *
 * TODO: it may be more advisable to not route everything through a trampoline class, instead overwriting the callee
 */
public class PlatformDispatch {
	public static void pingHud$updatePing(MutableInt currentServerPing) {
		throw BridgeUtil.noImpl();
	}

	public static AxoSprite.Dynamic ipHud$getServerIcon() {
		throw BridgeUtil.noImpl();
	}
}
