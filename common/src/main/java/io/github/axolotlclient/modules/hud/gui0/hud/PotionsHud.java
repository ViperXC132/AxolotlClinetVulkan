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

package io.github.axolotlclient.modules.hud.gui0.hud;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.bridge.Platform;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffects;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui0.layout.CardinalOrder;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import java.util.ArrayList;
import java.util.List;

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

	public PotionsHud() {
		super(50, 200, false);
	}

	@Override
	public void renderComponent(AxoRenderContext graphics, float delta) {
		if (client.br$getPlayer() == null) {
			return;
		}

		final var effects = new ArrayList<>(client.br$getPlayer().br$getStatusEffects());
		if (effects.isEmpty()) {
			return;
		}

		renderEffects(graphics, effects, delta);
	}

	private void renderEffects(AxoRenderContext graphics, List<AxoStatusEffectInstance> effects, float tickDelta) {
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
				lastPos += renderPotion(graphics, effect, x + lastPos + 1, y + 1, tickDelta);
			} else {
				renderPotion(graphics, effect, x + 1, y + 1 + lastPos, tickDelta);
				lastPos += 20;
			}
		}
	}

	private int calculateWidth(List<AxoStatusEffectInstance> effects) {
	/*	if ((order.get()).isXAxis()) {
			if (iconsOnly.get()) {
				return 20 * effects.size() + 2;
			}
			if (!showEffectName.get()) {
				return 50 * effects.size() + 2;
			}
			return effects.stream().map(effect -> Component.translatable(effect.getDescriptionId()).append(" ")
				.append(Util.toRoman(effect.getAmplifier()))).mapToInt(client.font::width).map(i -> i + 20).sum() + 2;
		} else {
			if (iconsOnly.get()) {
				return 20;
			}
			if (!showEffectName.get()) {
				return 50;
			}
			return effects.stream().map(effect -> Component.translatable(effect.getDescriptionId()).append(" ")
				.append(Util.toRoman(effect.getAmplifier()))).map(client.font::width).max(Integer::compare).orElse
				(38) +
				22;
		}*/

		return 0;
	}

	private int calculateHeight(List<AxoStatusEffectInstance> effects) {
		if ((order.get()).isXAxis()) {
			return 22;
		} else {
			return 20 * effects.size() + 2;
		}
	}

	private int renderPotion(AxoRenderContext graphics, AxoStatusEffectInstance effect, int x, int y,
							 float tickDelta) {
		final var sprite = effect.br$getType().br$getSprite();

		graphics.br$drawTexture(x, y, 18, 18, sprite);
		if (!iconsOnly.get()) {
			float tickrate = Platform.tickRate();

			return 0;

			/* TODO
			if (showEffectName.get()) {
				AxoText string = ;

				graphics.br$drawString(string.br$getRawString(), x + 19, y + 1, textColor.get().toInt(), shadow.get());
				AxoText duration = MobEffectUtil.formatDuration(effect, 1, tickrate);
				graphics.br$drawString(duration.br$getRawString(), x + 19, y + 1 + 10, timerTextColor.get().toInt(),
				shadow.get());
			} else {
				graphics.br$drawString(MobEffectUtil.formatDuration(effect, 1, tickrate), x + 19, y + 5,
					timerTextColor.get().toInt(), shadow.get()
				);
			}*/
		}

		return 0;
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		final var effect = AxoStatusEffectInstance.create(AxoStatusEffects.SPEED, 9999);
		final var jump = AxoStatusEffectInstance.create(AxoStatusEffects.JUMP_BOOST, 99999);
		final var haste = AxoStatusEffectInstance.create(AxoStatusEffects.HASTE, -1);
		renderEffects(graphics, List.of(effect, jump, haste), 0);
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
