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

package io.github.axolotlclient.modules.hud.gui.hud;

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffects;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.CardinalOrder;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.CommonUtil;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {
	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "potionshud");

	private final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();
	private final EnumOption<CardinalOrder> order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);

	private final BooleanOption iconsOnly = new BooleanOption("iconsonly", false);
	private final BooleanOption showEffectName = new BooleanOption("showEffectNames", true);
	private final ColorOption timerTextColor = new ColorOption("potionshud.timer_text_color", Color.parse("#7F7F7F"));

	private static AxoText formatNameAndAmplifier(AxoStatusEffectInstance effect) {
		return effect.br$getType().br$getDisplayName().br$copy()
			.br$append(" ")
			.br$append(CommonUtil.toRoman(effect.br$getAmplifier() + 1));
	}

	public PotionsHud() {
		super(50, 200, true);
		background = new BooleanOption("background", false);
	}

	@Override
	public void renderComponent(AxoRenderContext graphics, float delta) {
		final var player = client.br$getPlayer();
		if(player == null) {
			return;
		}

		final var effects = player.br$getStatusEffects();

		renderEffects(graphics, effects);
	}

	private void renderEffects(AxoRenderContext graphics, List<AxoStatusEffectInstance> effects) {
		int calcWidth = calculateWidth(effects);
		int calcHeight = calculateHeight(effects);
		boolean changed = false;
		if (calcWidth != width) {
			setWidth(calcWidth);
			changed = true;
		}
		if (calcHeight != height) {
			setHeight(calcHeight);
			changed = true;
		}
		if (changed) {
			onBoundsUpdate();
		}
		int lastPos = 0;
		CardinalOrder direction = (order.get());

		Rectangle bounds = getBounds();
		int x = bounds.x();
		int y = bounds.y();
		for (int i = 0; i < effects.size(); i++) {
			final var effect = effects.get(direction.getDirection() == -1 ? i : effects.size() - i - 1);
			if (direction.isXAxis()) {
				renderPotion(graphics, effect, x + lastPos + 1, y + 1);
				int nameWidth = 0;
				if (!iconsOnly.get()) {
					nameWidth += graphics.br$getFont().br$getWidth(effect.br$formatDuration()) + 1;
					if (showEffectName.get()) {
						nameWidth = Math.max(nameWidth, client.br$getFont().br$getWidth(formatNameAndAmplifier(effect)));
					}
				}
				lastPos += 20 + nameWidth;
			} else {
				renderPotion(graphics, effect, x + 1, y + 1 + lastPos);
				lastPos += 20;
			}
		}
	}

	private int calculateWidth(List<AxoStatusEffectInstance> effects) {
		final var widthStreamFullName = effects.stream()
			.map(PotionsHud::formatNameAndAmplifier)
			.mapToInt(client.br$getFont()::br$getWidth);
		final var widthStreamDuration = effects.stream()
			.map(AxoStatusEffectInstance::br$formatDuration)
			.mapToInt(client.br$getFont()::br$getWidth);

		if (order.get().isXAxis()) {
			if (iconsOnly.get()) {
				return 20 * effects.size() + 2;
			}

			if (!showEffectName.get()) {
				return widthStreamDuration.map(i -> i + 20).sum() + 3;
			}

			return widthStreamFullName.map(i -> i + 20).sum() + 2;
		} else {
			if (iconsOnly.get()) {
				return 20;
			}

			if (!showEffectName.get()) {
				return widthStreamDuration.max().orElse(0) + 22;
			}
			return widthStreamFullName.max().orElse(0) + 22;
		}
	}

	private int calculateHeight(List<AxoStatusEffectInstance> effects) {
		if ((order.get()).isXAxis()) {
			return 20;
		} else {
			return 20 * effects.size() + 2;
		}
	}

	private void renderPotion(AxoRenderContext graphics, AxoStatusEffectInstance effect, int x, int y) {
		final var type = effect.br$getType();

		graphics.br$drawTexture(x, y, 18, 18, type.br$getSprite());

		if (!iconsOnly.get()) {
			if (showEffectName.get()) {
				graphics.br$drawString(formatNameAndAmplifier(effect), x + 19, y + 1, textColor.get().toInt(), shadow.get());
				graphics.br$drawString(effect.br$formatDuration(), x + 19, y + 1 + 10,
					timerTextColor.get().toInt(), shadow.get());
			} else {
				graphics.br$drawString(effect.br$formatDuration(), x + 19, y + 5,
					timerTextColor.get().toInt(), shadow.get());
			}
		}
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		AxoStatusEffectInstance effect = AxoStatusEffectInstance.create(AxoStatusEffects.SPEED, 9999);
		AxoStatusEffectInstance jump = AxoStatusEffectInstance.create(AxoStatusEffects.JUMP_BOOST, 99999);
		AxoStatusEffectInstance haste = AxoStatusEffectInstance.create(AxoStatusEffects.HASTE, -1);
		renderEffects(graphics, List.of(effect, jump, haste));
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(anchor);
		options.add(order);
		options.add(iconsOnly);
		options.add(showEffectName);
		options.add(timerTextColor);
		return options;
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public AnchorPoint getAnchor() {
		return (anchor.get());
	}
}
