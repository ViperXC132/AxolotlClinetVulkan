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

package io.github.axolotlclient.modules.hud.gui0.entry;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoI18n;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui0.layout.Justification;
import io.github.axolotlclient.modules.hud.util.DefaultOptions0;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public abstract class SimpleTextHudEntry extends TextHudEntry implements DynamicallyPositionable {

	protected final EnumOption<Justification> justification = new EnumOption<>("justification", Justification.class,
		Justification.CENTER);
	protected final EnumOption<AnchorPoint> anchor = DefaultOptions0.getAnchorPoint();
	protected final BooleanOption showBrackets = new BooleanOption("show_brackets", false);

	private final IntegerOption minWidth;

	public SimpleTextHudEntry() {
		this(53, 13, true);
	}

	protected SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
		super(width, height, backgroundAllowed);
		minWidth = new IntegerOption("minwidth", width, 1, 300);
	}

	protected SimpleTextHudEntry(int width, int height) {
		this(width, height, true);
	}

	@Override
	public void renderComponent(AxoRenderContext render, float delta) {
		render.glEnableBlend();
		// render.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// render.glDisableTexture();
		DrawPosition pos = getPos();
		String value = wrapWithBrackets(getValue());

		int valueWidth = render.getTextRenderer().getWidth(value);
		int elementWidth = valueWidth + 4;

		int min = minWidth.get();
		if (elementWidth < min) {
			if (width != min) {
				setWidth(min);
				onBoundsUpdate();
			}
		} else if (elementWidth != width) {
			setWidth(elementWidth);
			onBoundsUpdate();
		}
		render.drawString(value,
			pos.x() + justification.get().getXOffset(valueWidth, getWidth() - 4) + 2,
			pos.y() + (Math.round((float) getHeight() / 2)) - 4, getTextColor().toInt(), shadow.get());
		// render.glEnableTexture();
		// render.glDisableBlend();
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
		DrawPosition pos = getPos();
		String value = wrapWithBrackets(getPlaceholder());
		ctx.drawString(value, pos.x() + justification.get().getXOffset(value, getWidth() - 4) + 2,
			pos.y() + (Math.round((float) getHeight() / 2)) - 4, getTextColor().toInt(), shadow.get());
	}

	protected String wrapWithBrackets(String value) {
		if (showBrackets.get()) {
			return AxoI18n.translate("bracket_format", value);
		}
		return value;
	}

	public abstract String getPlaceholder();

	public abstract String getValue();

	public Color getTextColor() {
		return textColor.get();
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(justification);
		options.add(anchor);
		options.add(minWidth);
		options.add(showBrackets);
		return options;
	}

	@Override
	public AnchorPoint getAnchor() {
		return anchor.get();
	}
}
