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

package io.github.axolotlclient.modules.hud.gui0.hud.item;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.bridge.item.AxoEnchants;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.ItemUtil;
import java.util.List;
import java.util.stream.Stream;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class ArmorHud extends TextHudEntry implements DynamicallyPositionable {
	private static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "armorhud");

	private static final List<AxoItemStack> PLACEHOLDER_STACKS = List.of(
		AxoItemStack.of(AxoItems.IRON_SWORD),
		AxoItemStack.of(AxoItems.IRON_BOOTS),
		AxoItemStack.of(AxoItems.IRON_LEGGINGS),
		AxoItemStack.of(AxoItems.IRON_CHESTPLATE),
		AxoItemStack.of(AxoItems.IRON_HELMET)
	);

	protected final BooleanOption showProtLvl = new BooleanOption("showProtectionLevel", false);
	private final BooleanOption showDurabilityNumber = new BooleanOption("show_durability_num", false);
	private final BooleanOption showMaxDurabilityNumber = new BooleanOption("show_max_durability_num", false);
	private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class, AnchorPoint.TOP_RIGHT);

	public ArmorHud() {
		super(20, 100, true);
	}

	private int computeLabelWidth(AxoRenderContext context, List<AxoItemStack> items) {
		boolean showDurability = showDurabilityNumber.get();
		boolean showMaxDurability = showMaxDurabilityNumber.get();
		return showDurability || showMaxDurability ? items
			.stream()
			.filter(x -> !x.br$isEmpty())
			.map(this::getItemString)
			.mapToInt(text -> context.br$getFont().br$getWidth(text) + 2)
			.max()
			.orElse(0) : 0;
	}

	private String getItemString(AxoItemStack stack) {
		boolean showDurability = showDurabilityNumber.get();
		boolean showMaxDurability = showMaxDurabilityNumber.get();
		if (showDurability && showMaxDurability) {
			return (stack.br$getMaxDamage() - stack.br$getDamage()) + "/" + stack.br$getMaxDamage();
		}

		return String.valueOf(showDurability ? stack.br$getMaxDamage() - stack.br$getDamage() : stack.br$getMaxDamage());
	}

	private void renderDurabilityNumber(AxoRenderContext context, AxoItemStack stack, int x, int y) {
		boolean showDurability = showDurabilityNumber.get();
		boolean showMaxDurability = showMaxDurabilityNumber.get();
		if (stack == null || !(showMaxDurability || showDurability) || stack.br$getMaxDamage() == 0) {
			return;
		}
		String text = getItemString(stack);
		int textY = y + 10 - context.br$getFont().br$getFontHeight() / 2;
		float f = (float) stack.br$getDamage();
		float g = (float) stack.br$getMaxDamage();
		float h = Math.max(0.0F, (g - f) / g);
		int j = java.awt.Color.HSBtoRGB(h / 3.0F, 1.0F, 1.0F);
		context.br$drawString(text, x, textY, (((255 << 8) + (j >> 16 & 255) << 8) + (j >> 8 & 255) << 8) + (j & 255), false);
	}

	private void renderItem(AxoRenderContext context, AxoItemStack stack, int x, int y, int offset, String mainItemOverride) {
		renderDurabilityNumber(context, stack, x, y);
		x += offset;
		context.br$renderGuiItemModel(stack, x, y);
		context.br$renderGuiItemOverlay(stack, x, y, mainItemOverride, textColor.get().toInt(), shadow.get());
	}

	private void renderComponent(AxoRenderContext context, List<AxoItemStack> items, int mainItemCountOverride) {
		int width = 20;
		int labelWidth = computeLabelWidth(context, items);
		width += labelWidth;
		if (width != getWidth()) {
			setWidth(width);
			onBoundsUpdate();
		}
		DrawPosition pos = getPos();
		int lastY = 2 + (4 * 20);

		for (int i = 0; i < items.size(); i++) {
			AxoItemStack item = items.get(i);
			if (!item.br$isEmpty()) {

				final var itemForRendering = item.br$copy();

				if (showProtLvl.get()) {
					if (itemForRendering.br$hasEnchantment(AxoEnchants.PROTECTION)) {
						itemForRendering.br$setCount(itemForRendering.br$getEnchantment(AxoEnchants.PROTECTION));
					}
				}

				// first item
				if (i == 0) {
					String override = mainItemCountOverride == 1 ? null : String.valueOf(mainItemCountOverride);
					renderItem(context, itemForRendering, pos.x() + 2, lastY + pos.y(), labelWidth, override);
				} else {
					renderItem(context, itemForRendering, pos.x() + 2, lastY + pos.y(), labelWidth, null);
				}
			}

			lastY = lastY - 20;
		}
	}

	@Override
	public void renderComponent(AxoRenderContext context, float delta) {
		if (client.br$getPlayer() == null) {
			return;
		}

		AxoPlayerInventory inventory = client.br$getPlayer().br$getInventory();

		renderComponent(
			context,
			Stream.concat(
				Stream.of(inventory.br$getMainHand()),
				inventory.br$getArmor().stream()
			).toList(),
			ItemUtil.getTotal(inventory, inventory.br$getMainHand().br$getItem())
		);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
		renderComponent(context, PLACEHOLDER_STACKS, 1);
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(showProtLvl);
		options.add(showDurabilityNumber);
		options.add(showMaxDurabilityNumber);
		options.add(anchor);
		return options;
	}

	public AnchorPoint getAnchor() {
		return anchor.get();
	}
}
