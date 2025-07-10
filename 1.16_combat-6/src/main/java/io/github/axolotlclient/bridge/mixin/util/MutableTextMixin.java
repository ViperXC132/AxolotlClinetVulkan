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

package io.github.axolotlclient.bridge.mixin.util;

import io.github.axolotlclient.bridge.util.AxoText;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MutableText.class)
public interface MutableTextMixin extends AxoText.Mutable {
	@Shadow
	MutableText append(Text text);

	@Shadow
	MutableText setStyle(net.minecraft.text.Style style);

	@Override
	default Mutable br$append(AxoText child) {
		return append((Text) child);
	}

	@Override
	default Mutable br$setStyle(Style style) {
		return setStyle((net.minecraft.text.Style) style);
	}
}
